package br.com.escolaDuartina.security;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import br.com.escolaDuartina.dto.UsuarioResumo;
import br.com.escolaDuartina.model.Aluno;
import br.com.escolaDuartina.model.User;
import br.com.escolaDuartina.service.LogSystemService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LogSystemInterceptor implements HandlerInterceptor {
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
		String path = request.getRequestURI().substring(request.getContextPath().length());
		if ("/logs".equals(path) || path.startsWith("/logs/")
				|| ("GET".equalsIgnoreCase(request.getMethod()) && "/usuario/pendentes".equals(path))) return;

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()
				|| !(authentication.getPrincipal() instanceof UserDetails userDetails)) return;

		String metodo = request.getMethod().toUpperCase(Locale.ROOT);
		String classe = classeEntidade(handler, path);
		String status = String.valueOf(response.getStatus());
		List<String> acoes = new ArrayList<>();

		if (HttpMethod.PATCH.matches(metodo) || HttpMethod.PUT.matches(metodo)) {
			Object antigo = request.getAttribute(OLD_OBJECT);
			Object novo = request.getAttribute(NEW_OBJECT);
			acoes.add("Atualização valor antigo: " + classe + " -> " + representar(antigo, request)
					+ ", código de método de retorno: " + status);
			acoes.add("Atualização valor novo: " + classe + " -> " + representar(novo, request)
					+ ", código de método de retorno: " + status);
		} else {
			String rotulo = rotulo(metodo);
			Object objeto = objetoAcao(request, response, path, metodo);
			acoes.add(rotulo + ": " + classe + " -> " + representar(objeto, request)
					+ ", código de método de retorno: " + status);
		}

		for (String acao : acoes) {
			try {
				logService.registrar(userDetails.getUsername(), acao);
			} catch (RuntimeException logError) {
				// Falha no registro não deve derrubar a requisição original.
			}
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

	private Object objetoAcao(HttpServletRequest request, HttpServletResponse response, String path, String metodo) {
		Object capturado = request.getAttribute(HttpMethod.DELETE.matches(metodo) ? OLD_OBJECT : NEW_OBJECT);
		if (capturado != null) return capturado;
		String json = responseJson(response);
		if (!StringUtils.hasText(json)) json = requestBody(request);
		if (!StringUtils.hasText(json)) return "id=" + ultimoSegmento(path);
		return converterObjetos(json, path);
	}

	private Object converterObjetos(String json, String path) {
		try {
			JsonNode root = objectMapper.readTree(json);
			Class<?> tipo = path.startsWith("/alunos") ? Aluno.class :
					path.startsWith("/usuario") ? UsuarioResumo.class : null;
			if (tipo == null) {
				JsonNode seguro = root.deepCopy();
				mascararCredenciais(seguro);
				return objectMapper.writeValueAsString(seguro);
			}
			if (root.isObject() && root.has("content") && root.get("content").isArray()) {
				root = root.get("content");
			}
			if (root.isArray()) {
				List<String> representacoes = new ArrayList<>();
				for (JsonNode item : root) representacoes.add(converterUm(item, tipo).toString());
				return representacoes;
			}
			return converterUm(root, tipo);
		} catch (Exception ignored) {
			return json;
		}
	}

	private Object converterUm(JsonNode node, Class<?> tipo) throws Exception {
		JsonNode copia = node.deepCopy();
		mascararCredenciais(copia);
		return objectMapper.treeToValue(copia, tipo);
	}

	private String representar(Object objeto, HttpServletRequest request) {
		if (objeto == null) return "objeto não disponível";
		if (objeto instanceof String texto) return texto;
		if (objeto instanceof Aluno || objeto instanceof User || objeto instanceof UsuarioResumo) {
			return objeto.toString();
		}
		if (objeto instanceof List<?> lista) {
			return lista.stream().map(item -> item == null ? "null" : item.toString()).toList().toString();
		}
		return objeto.toString();
	}

	private String responseJson(HttpServletResponse response) {
		if (response instanceof ContentCachingResponseWrapper wrapper) {
			byte[] bytes = wrapper.getContentAsByteArray();
			if (bytes.length == 0) return "";
			return new String(bytes, StandardCharsets.UTF_8);
		}
		return "";
	}

	private String requestBody(HttpServletRequest request) {
		if (!(request instanceof ContentCachingRequestWrapper wrapper)) return "";
		byte[] bytes = wrapper.getContentAsByteArray();
		return bytes.length == 0 ? "" : new String(bytes, StandardCharsets.UTF_8);
	}

	private void mascararCredenciais(JsonNode node) {
		if (node instanceof ObjectNode objectNode) {
			objectNode.properties().forEach(entry -> {
				String campo = entry.getKey().toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "");
				if (campo.contains("senha") || campo.contains("password") || campo.contains("token")
						|| campo.contains("secret")) objectNode.put(entry.getKey(), "[REDACTED]");
				else mascararCredenciais(entry.getValue());
			});
		} else if (node != null && node.isArray()) {
			node.forEach(this::mascararCredenciais);
		}
	}

	private String ultimoSegmento(String path) {
		String[] segmentos = path.split("/");
		for (int i = segmentos.length - 1; i >= 0; i--) if (!segmentos[i].isBlank()) return segmentos[i];
		return "não informado";
	}
}
