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

		if (HttpMethod.POST.matches(request.getMethod()) && "/autentica/authenticate/modal-entrar".equals(path)) {
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
			acoes.add("Atualização valor antigo: " + classe + " -> " + representar(antigo)
					+ ", HTTP " + status);
			acoes.add("Atualização valor novo: " + classe + " -> " + (novo == null
					? dadosResposta(response, path, status) : representar(novo)) + ", HTTP " + status);
		} else {
			String rotulo = rotulo(metodo);
			Object objeto = request.getAttribute(HttpMethod.DELETE.matches(metodo) ? OLD_OBJECT : NEW_OBJECT);
			String dados = objeto == null ? dadosResposta(response, path, status) : representar(objeto);
			acoes.add(rotulo + ": " + classe + " -> " + dados + ", HTTP " + status);
		}

		try {
			logService.registrar(userDetails.getUsername(), acoes);
		} catch (RuntimeException logError) {
			// A resposta original deve ser preservada se o log falhar.
		}
	}

	private String rotulo(String metodo) {
		if (HttpMethod.POST.matches(metodo)) return "Inclusão";
		if (HttpMethod.DELETE.matches(metodo)) return "Exclusão";
		if (HttpMethod.GET.matches(metodo)) return "Consulta";
		return "Ação";
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
		if ("/home".equals(path) || "/welcome".equals(path)) return json;
		try {
			JsonNode dados = objectMapper.readTree(json);
			if ("/leitura-pdf".equals(path) && dados != null) {
				ObjectNode resumo = objectMapper.createObjectNode();
				resumo.set("nome", dados.get("nome"));
				resumo.set("totalPaginas", dados.get("totalPaginas"));
				return resumo.toString();
			}
			if (dados != null && dados.has("content") && dados.get("content").isArray()) {
				dados = dados.get("content");
			}
			return representar(dados);
		} catch (Exception invalidJson) {
			return "objeto não disponível";
		}
	}

	private String representar(Object objeto) {
		if (objeto == null) return "objeto não disponível";
		JsonNode dados = objectMapper.valueToTree(objeto);
		mascararCredenciais(dados);
		return dados.toString();
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
