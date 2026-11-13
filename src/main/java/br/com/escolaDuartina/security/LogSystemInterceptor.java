package br.com.escolaDuartina.security;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingResponseWrapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import br.com.escolaDuartina.service.LogSystemService;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LogSystemInterceptor implements HandlerInterceptor {
	public static final String LOGIN_EMAIL_ATTRIBUTE = "logSystem.loginEmail";
	private static final String OLD_OBJECT = "logSystem.oldObject";
	private static final String NEW_OBJECT = "logSystem.newObject";

	private final LogSystemService logService;
	private final ObjectMapper objectMapper = new ObjectMapper();

	public LogSystemInterceptor(LogSystemService logService) {
		this.logService = logService;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception exception) {
		if (exception instanceof AccessDeniedException || request.getDispatcherType() == DispatcherType.ERROR) return;
		String path = request.getRequestURI().substring(request.getContextPath().length());
		if ("/logs".equals(path) || path.startsWith("/logs/")) return;

		if (HttpMethod.POST.matches(request.getMethod()) && ("/autentica/authenticate/modal-entrar".equals(path)
				|| "/autentica/authenticate".equals(path))) {
			int status = response.getStatus();
			if (exception == null && status >= 200 && status < 300
					&& request.getAttribute(LOGIN_EMAIL_ATTRIBUTE) instanceof String email) {
				try {
					logService.registrarLogin(email, status);
				} catch (RuntimeException logError) {
					// A resposta de login deve ser preservada se o log falhar.
				}
			}
			return;
		}

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()
				|| !(authentication.getPrincipal() instanceof UserDetails userDetails)) return;

		String metodo = request.getMethod().toUpperCase(Locale.ROOT);
		String classe = classeEntidade(handler, path);
		int status = exception != null && response.getStatus() < 400
				? HttpServletResponse.SC_INTERNAL_SERVER_ERROR : response.getStatus();
		if (status == HttpServletResponse.SC_OK && HttpMethod.GET.matches(metodo)
				&& "/usuario/pendentes".equals(path) && "true".equals(request.getParameter("aposLogin"))) {
			try {
				logService.registrarConsultaAposLogin(userDetails.getUsername(), status);
			} catch (RuntimeException logError) {
				// A resposta original deve ser preservada se o log falhar.
			}
			return;
		}

		List<String> acoes = new ArrayList<>();

		if (HttpMethod.PATCH.matches(metodo) || HttpMethod.PUT.matches(metodo)) {
			Object antigo = request.getAttribute(OLD_OBJECT);
			Object novo = request.getAttribute(NEW_OBJECT);
			String antes = antigo == null ? "dados anteriores indisponíveis" : resumo(antigo);
			Object objetoNovo = novo == null ? dadosResposta(response, path, status) : novo;
			String depois = objetoNovo instanceof String texto ? resumoTexto(texto) : resumo(objetoNovo);
			if (HttpMethod.PATCH.matches(metodo) && path.matches("/usuario/\\d+/aprovacao")) {
				depois = resumoAprovacao(novo, antigo);
			}
			String acao = "Atualizou " + nomeDaEntidade(classe) + " | Antes: " + antes + " | Depois: " + depois
					+ " | Resultado: " + resultado(status);
			acoes.add(acao);
		} else {
			String rotulo = rotulo(metodo);
			Object objeto = request.getAttribute(HttpMethod.DELETE.matches(metodo) ? OLD_OBJECT : NEW_OBJECT);
			Object dados = objeto == null ? dadosResposta(response, path, status) : objeto;
			acoes.add(rotulo + " " + nomeDaEntidade(classe) + ": " + resumoOuTexto(dados)
					+ " | Resultado: " + resultado(status));
		}

		try {
			logService.registrar(userDetails.getUsername(), acoes);
		} catch (RuntimeException logError) {
			// A resposta original deve ser preservada se o log falhar.
		}
	}

	private String rotulo(String metodo) {
		if (HttpMethod.POST.matches(metodo)) return "Cadastrou";
		if (HttpMethod.DELETE.matches(metodo)) return "Excluiu";
		if (HttpMethod.GET.matches(metodo)) return "Consultou";
		return "Ação";
	}

	private String nomeDaEntidade(String classe) {
		return switch (classe) {
		case "Aluno" -> "aluno";
		case "User" -> "usuário";
		case "LogSystem" -> "log do sistema";
		default -> classe.toLowerCase(Locale.ROOT);
		};
	}

	private String resultado(int status) {
		return status >= 200 && status < 300 ? "concluído" : "falhou (HTTP " + status + ")";
	}

	private String resumoOuTexto(Object objeto) {
		return objeto instanceof String texto ? resumoTexto(texto) : resumo(objeto);
	}

	private String resumoTexto(String texto) {
		if (texto == null || texto.isBlank() || "objeto não disponível".equals(texto)) return "detalhes indisponíveis";
		try {
			return resumo(objectMapper.readTree(texto));
		} catch (Exception invalidJson) {
			return texto;
		}
	}

	private String resumo(Object objeto) {
		if (objeto == null) return "detalhes indisponíveis";
		JsonNode dados = objeto instanceof JsonNode node ? node : objectMapper.valueToTree(objeto);
		mascararCredenciais(dados);
		if (dados.isArray()) return "consulta retornou " + dados.size() + " registro(s)";
		if (!dados.isObject()) return dados.asText("detalhes indisponíveis");
		if (dados.has("accessToken") || dados.has("refreshToken")) return "sessão autenticada";
		if (dados.has("approved") || dados.has("aprovado")) {
			return (dados.path("approved").asBoolean(dados.path("aprovado").asBoolean())
					? "usuário aprovado" : "aprovação pendente") + resumoIdentificacao(dados);
		}

		List<String> campos = new ArrayList<>();
		adicionarCampo(dados, campos, "nome", "nome");
		adicionarCampo(dados, campos, "name", "nome");
		adicionarCampo(dados, campos, "rm", "RM");
		adicionarCampo(dados, campos, "registro_matricula", "RM");
		adicionarCampo(dados, campos, "unidade", "unidade");
		adicionarCampo(dados, campos, "data_nascimento", "nascimento");
		adicionarCampo(dados, campos, "dataNascimento", "nascimento");
		adicionarCampo(dados, campos, "email", "e-mail");
		adicionarCampo(dados, campos, "approved", "aprovação");
		adicionarCampo(dados, campos, "aprovado", "aprovação");
		adicionarCampo(dados, campos, "id", "ID");
		adicionarCampo(dados, campos, "totalPaginas", "total de páginas");
		return campos.isEmpty() ? "registro atualizado" : String.join(" | ", campos);
	}

	private String resumoIdentificacao(JsonNode dados) {
		List<String> campos = new ArrayList<>();
		adicionarCampo(dados, campos, "name", "nome");
		adicionarCampo(dados, campos, "email", "e-mail");
		adicionarCampo(dados, campos, "id", "ID");
		return campos.isEmpty() ? "" : " | " + String.join(" | ", campos);
	}

	private String resumoAprovacao(Object novo, Object antigo) {
		JsonNode atualizado = novo == null ? null : objectMapper.valueToTree(novo);
		JsonNode anterior = antigo == null ? null : objectMapper.valueToTree(antigo);
		if (atualizado == null || anterior == null) return "status de aprovação atualizado";
		boolean aprovada = atualizado.path("approved").asBoolean();
		String statusNovo = aprovada ? "aprovado" : "pendente";
		String statusAnterior = anterior.path("approved").asBoolean() ? "aprovado" : "pendente";
		return statusAnterior + " → " + statusNovo + resumoIdentificacao(atualizado);
	}

	private void adicionarCampo(JsonNode dados, List<String> campos, String chave, String rotulo) {
		JsonNode valor = dados.get(chave);
		if (valor != null && !valor.isNull() && !valor.isContainerNode()) {
			String texto = valor.asText();
			if (!texto.isBlank() && campos.stream().noneMatch(campo -> campo.startsWith(rotulo + ":"))) {
				campos.add(rotulo + ": " + texto);
			}
		}
	}

	private String classeEntidade(Object handler, String path) {
		if (path.startsWith("/alunos")) return "Aluno";
		if (path.startsWith("/usuario")) return "User";
		if (handler instanceof HandlerMethod handlerMethod) {
			return handlerMethod.getBeanType().getSimpleName().replaceFirst("Controller$", "");
		}
		return "Endpoint";
	}

	private String dadosResposta(HttpServletResponse response, String path, int status) {
		if (status >= 400) return "objeto não disponível";
		if ("/leitura-pdf/voz".equals(path)) return "Áudio neural gerado";
		String json = responseJson(response);
		if ("/home".equals(path) || "/welcome".equals(path) || "/autentica/refresh".equals(path)) {
			return "operação concluída";
		}
		try {
			JsonNode dados = objectMapper.readTree(json);
			if ("/leitura-pdf".equals(path) && dados != null) {
				ObjectNode resumo = objectMapper.createObjectNode();
				resumo.set("nome", dados.get("nome"));
				resumo.set("totalPaginas", dados.get("totalPaginas"));
				return resumo(resumo);
			}
			if (dados != null && dados.has("content") && dados.get("content").isArray()) {
				return "consulta retornou " + dados.path("totalElements").asInt(dados.get("content").size())
						+ " registro(s)";
			}
			return resumo(dados);
		} catch (Exception invalidJson) {
			return "objeto não disponível";
		}
	}

	private String responseJson(HttpServletResponse response) {
		if (response instanceof ContentCachingResponseWrapper wrapper) {
			byte[] bytes = wrapper.getContentAsByteArray();
			if (bytes.length == 0) return "";
			return new String(bytes, StandardCharsets.UTF_8);
		}
		return "";
	}

	private void mascararCredenciais(JsonNode node) {
		if (node instanceof ObjectNode objectNode) {
			objectNode.properties().forEach(entry -> {
				String campo = entry.getKey().toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "");
				if (campo.contains("senha") || campo.contains("password") || campo.contains("passwd")
						|| campo.contains("token") || campo.contains("secret") || campo.contains("credential")
						|| campo.equals("authorization") || campo.contains("apikey") || campo.contains("privatekey")) {
					objectNode.put(entry.getKey(), "[REDACTED]");
				} else {
					mascararCredenciais(entry.getValue());
				}
			});
		} else if (node != null && node.isArray()) {
			node.forEach(this::mascararCredenciais);
		}
	}

}
