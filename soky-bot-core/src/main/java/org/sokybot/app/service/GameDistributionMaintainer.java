package org.sokybot.app.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import javax.print.Doc;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.Document;
import org.dizitart.no2.collection.DocumentCursor;
import org.dizitart.no2.collection.NitriteCollection;
import org.dizitart.no2.filters.EqualsFilter;
import org.dizitart.no2.filters.Filter;
import org.dizitart.no2.filters.FluentFilter;
import org.dizitart.no2.filters.LogicalFilter;
import org.dizitart.no2.repository.Cursor;
import org.dizitart.no2.repository.ObjectRepository;
import org.sokybot.domain.SkillEntity;
import org.sokybot.domain.items.ItemEntity;
import org.sokybot.pk2extractor.IEntityExtractorFactory;
import org.sokybot.pk2extractor.IMediaPk2;
import org.sokybot.pk2extractor.MediaPk2;
import org.sokybot.utils.Helper;
import org.springframework.aop.support.ClassFilters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/*
 * GameEntitiesVersion
 * 
 * 
 */
@Service
public class GameDistributionMaintainer implements IGameDistributionMaintainer {

	@Autowired
	@Qualifier("gameVersionRegistry")
	private NitriteCollection gameDistributionRegistry;

	@Autowired
	private Nitrite db;

	@Autowired
	private IEntityExtractorFactory entityExtractorFactory;

	private Map<String, Lock> lockCach;

	private void lock(String gameDist) {

		if (!this.lockCach.containsKey(gameDist)) {
			this.lockCach.put(gameDist, new ReentrantLock());
		}

		this.lockCach.get(gameDist).lock();
	}

	private void unlock(String gameDist) {

		if (this.lockCach.containsKey(gameDist)) {
			this.lockCach.get(gameDist).unlock();
		}
	}

	private boolean isRepositoriesConsistentWith(String path) {

		DocumentCursor c = this.gameDistributionRegistry.find(FluentFilter.where("game-path").eq(path));

		if (c.size() == 0)
			return true;

		IMediaPk2 mediaPk2 = this.entityExtractorFactory.getMediaPk2(path);

		int lastRegisteredVersion = c.firstOrNull().get("game-version", Integer.class);
		int actualGameVersion = mediaPk2.extractVersion();

		if (actualGameVersion != lastRegisteredVersion)
			return true;

		return false;

	}

	private boolean isValidDistribution(String path) {
		// this game distribution path is valid only if the passed path is extists and
		// is silkroad path
		return (Files.exists(Paths.get(path)) && Helper.isSilkraodDirectory(new File(path)));
	}

	private Optional<Document> findDocument(String gamePath) {

		return Optional.ofNullable(
				this.gameDistributionRegistry.find(FluentFilter.where("game-path").eq(gamePath)).firstOrNull());
	}

	private Document newGameDistributionReg(String gamePath, Integer ver) {
		return Document.createDocument().put("game-path", gamePath).put("game-version", ver);
	}

	@Override
	public void fit(String gamePath) {

		if (!isValidDistribution(gamePath)) {

			return;
		}

		try (IMediaPk2 mediaPk2 = this.entityExtractorFactory.getMediaPk2(gamePath)) {
			lock(gamePath);

			findDocument(gamePath).ifPresentOrElse(gameDoc -> {
				int actualGameVersion = mediaPk2.extractVersion();
				int registeredGameVersion = gameDoc.get("game-version", Integer.class);

				if (actualGameVersion != registeredGameVersion) {
					Integer gameVersion = extractEntites(gamePath);
					gameDoc.put("game-version", gameVersion);
				}

			}, () -> {
				// extract game
				Integer gameVersion = extractEntites(gamePath);
				this.gameDistributionRegistry.insert(newGameDistributionReg(gamePath, gameVersion));

			});

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			unlock(gamePath);
		}
	}

	private int extractEntites(String gamePath) {
		// first extract all entities from meida

		IMediaPk2 mediaPk2 = this.entityExtractorFactory.getMediaPk2(gamePath);

		ObjectRepository<ItemEntity> itemEntities = db.repository(ItemEntity.class).hasKey(gamePath).withTypeId("id")
				.get();

		ObjectRepository<SkillEntity> skillEntities = db.repository(SkillEntity.class).hasKey(gamePath)
				.withTypeId("refId").get();

		mediaPk2.getItemEntities().forEach((itemEntity) -> itemEntities.update(itemEntity, true));

		mediaPk2.getSkillEntities().forEach((skillEntity) -> skillEntities.update(skillEntity, true));

		return mediaPk2.extractVersion();

	}

	@Override
	public List<String> listAllDistributions() {
		return this.gameDistributionRegistry.find().toList().stream().map((doc) -> doc.get("game-path", String.class))
				.collect(Collectors.toList());
	}

	@Override
	public Integer getLastSupportedVersion(String gamePath) {

		Integer res = -1;

		if (isValidDistribution(gamePath))
			res = findDocument(gamePath).map((doc) -> doc.get("game-version", Integer.class)).orElse(res);

		return res;
	}

	@Override
	public boolean isConsistentWith(String gamePath) {
		throw new UnsupportedOperationException("isConsistentWith is unsupported");
	}

}
