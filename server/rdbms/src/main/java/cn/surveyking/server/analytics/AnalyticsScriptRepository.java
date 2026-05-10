package cn.surveyking.server.analytics;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class AnalyticsScriptRepository {

	private final ObjectMapper objectMapper;

	@Value("${surveyking.analytics.script-dir:./.local/surveyking/analytics/scripts}")
	private String scriptDir;

	private volatile Map<String, CachedScript> cache = new HashMap<>();

	public List<AnalyticsScriptDefinition> list() {
		reloadIfNeeded();
		List<AnalyticsScriptDefinition> scripts = new ArrayList<>();
		for (CachedScript cachedScript : cache.values()) {
			scripts.add(cachedScript.getDefinition());
		}
		scripts.sort(Comparator.comparing(AnalyticsScriptDefinition::getId));
		return scripts;
	}

	public AnalyticsScriptDefinition get(String id) {
		reloadIfNeeded();
		CachedScript script = cache.get(id);
		if (script == null) {
			throw new IllegalArgumentException("分析脚本不存在：" + id);
		}
		return script.getDefinition();
	}

	private synchronized void reloadIfNeeded() {
		Path dir = Paths.get(scriptDir);
		if (!Files.isDirectory(dir)) {
			cache = new HashMap<>();
			return;
		}
		Map<String, CachedScript> next = new HashMap<>();
		try (Stream<Path> paths = Files.list(dir)) {
			paths.filter(path -> path.getFileName().toString().endsWith(".json")).forEach(path -> {
				try {
					long lastModified = Files.getLastModifiedTime(path).toMillis();
					AnalyticsScriptDefinition definition = objectMapper.readValue(path.toFile(),
							AnalyticsScriptDefinition.class);
					CachedScript old = cache.get(definition.getId());
					if (old != null && old.getLastModified() == lastModified) {
						next.put(old.getDefinition().getId(), old);
						return;
					}
					validate(definition, path);
					next.put(definition.getId(), new CachedScript(path.toAbsolutePath().toString(), lastModified, definition));
				}
				catch (IOException e) {
					throw new IllegalStateException("加载分析脚本失败：" + path, e);
				}
			});
			cache = next;
		}
		catch (IOException e) {
			throw new IllegalStateException("读取分析脚本目录失败：" + scriptDir, e);
		}
	}

	private void validate(AnalyticsScriptDefinition definition, Path path) {
		if (definition.getId() == null || definition.getId().trim().isEmpty()) {
			throw new IllegalArgumentException("分析脚本缺少 id：" + path);
		}
		if (definition.getSql() == null || definition.getSql().trim().isEmpty()) {
			throw new IllegalArgumentException("分析脚本缺少 sql：" + definition.getId());
		}
		String sql = definition.getSql().trim().toLowerCase();
		if (!(sql.startsWith("select") || sql.startsWith("with")) || sql.contains(";")) {
			throw new IllegalArgumentException("分析脚本只允许单条 SELECT/WITH 查询：" + definition.getId());
		}
		if (!CollectionUtils.isEmpty(definition.getColumns())) {
			return;
		}
		definition.setColumns(new ArrayList<>());
	}

	@lombok.Value
	private static class CachedScript {

		String path;

		long lastModified;

		AnalyticsScriptDefinition definition;

	}

}
