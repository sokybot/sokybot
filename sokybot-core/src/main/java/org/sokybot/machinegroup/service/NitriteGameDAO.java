package org.sokybot.machinegroup.service;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.Document;
import org.dizitart.no2.collection.NitriteCollection;
import org.dizitart.no2.filters.FluentFilter;
import org.dizitart.no2.repository.ObjectRepository;
import org.sokybot.domain.DivisionInfo;
import org.sokybot.domain.SilkroadType;
import org.sokybot.domain.SkillEntity;
import org.sokybot.domain.items.ItemEntity;
import org.sokybot.exception.InvalidGameException;
import org.sokybot.pk2extractor.IEntityExtractorFactory;
import org.sokybot.pk2extractor.IMediaPk2;
import org.sokybot.pk2extractor.exception.Pk2ExtractionException;
import org.sokybot.service.IGameDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NitriteGameDAO implements IGameDAO {

	private final NitriteCollection gameVersionRegister;
	private final String gamePath;
	private final IEntityExtractorFactory entityExtractorFactory;

	private ObjectRepository<ItemEntity> itemEntities;
	private ObjectRepository<SkillEntity> skillEntities;
	private DivisionInfo divisionInfo;
	private SilkroadType silkroadType;
	private int port;
	private int version;

	// private SilkroadData sroData;

	@Autowired
	public NitriteGameDAO(@Value("${gamePath}") String gamePath, Nitrite db,
			@Qualifier("gameVersionRegister") NitriteCollection gameVersionRegister,
			IEntityExtractorFactory entityExtractorFactory) {
		this.gamePath = gamePath;
		this.gameVersionRegister = gameVersionRegister;
		this.entityExtractorFactory = entityExtractorFactory;
		this.itemEntities = db.repository(ItemEntity.class).hasKey(gamePath).withTypeId("refId").get();
		this.skillEntities = db.repository(SkillEntity.class).hasKey(gamePath).withTypeId("refId").get();

	}

	/**
	 * 
	 * if there is no data maintained into db from the referenced game directory
	 * then it will extract all data and register the current version of the target
	 * game
	 * 
	 * if there exists maintained data already extracted from the referenced game
	 * directory and these data is incompatible with the current game data then it
	 * will re-extract all data from the target game and register the current
	 * version for the target game .
	 *
	 * 
	 */
	@PostConstruct
	private void extractGameData() {

		try (IMediaPk2 mediaPk2 = this.entityExtractorFactory.getMediaPk2(this.gamePath)) {

			this.port = mediaPk2.extractPort();
			this.divisionInfo = mediaPk2.extractDivisionInfo();
			this.silkroadType = mediaPk2.extractType();
			this.version = mediaPk2.extractVersion();

			int registeredVer = getRegisteredVersion(this.gamePath);
			if (registeredVer == -1 || registeredVer != this.version) {
				transfer(mediaPk2);
				saveVersion();
			}

		} catch (Pk2ExtractionException ex) {
			// because we are working with invalid game so we need to clean our repose to
			// save storage
			destroy(null);
			throw new InvalidGameException("An error occurs during extracting game entities from  " + gamePath, ex,
					gamePath);
		} catch (IOException e) {
			// TODO how to swallow this exception ?
			e.printStackTrace();
		}

	}

	private void destroy(Document doc) {
		this.gameVersionRegister.remove(FluentFilter.where("game-path").eq(this.gamePath));
		this.itemEntities.clear();
		this.skillEntities.clear();

	}

	private void saveVersion() {
		Optional.ofNullable(
				this.gameVersionRegister.find(FluentFilter.where("game-path").eq(this.gamePath)).firstOrNull())
				.ifPresentOrElse((doc) -> doc.put("game-version", this.version), () -> this.gameVersionRegister.insert(
						Document.createDocument(Map.of("game-path", this.gamePath, "game-version", this.version))));
	}

	private int getRegisteredVersion(String gamePath) {

		return Optional
				.ofNullable(this.gameVersionRegister.find(FluentFilter.where("game-path").eq(gamePath)).firstOrNull())
				.map((doc) -> doc.get("game-version", Integer.class))
				.orElse(-1);
	}

	/**
	 * extract all known game entities and store them into a managed db , So that it
	 * becomes easy to manipulate this data in a reasonable way
	 * 
	 * 
	 * @param MediaPk2 to extract from it
	 */
	private void transfer(IMediaPk2 media) {
		media.getItemEntities().forEach((itemEntity) -> itemEntities.update(itemEntity, true));
		media.getSkillEntities().forEach((skillEntity) -> skillEntities.update(skillEntity, true));
	}

	@Override
	public Integer getVersion() {
		return this.version;
	}

	@Override
	public Integer getPort() {
		return this.port;
	}

	@Override
	public String getRndHost() {
		return getDivisionInfo().getDivisions().get(0).getRandomHost();

	}

	@Override
	public ItemEntity getItem(int refId) {
		return this.itemEntities.getById(refId);
	}

	@Override
	public SkillEntity getSkill(int refId) {
		return this.skillEntities.getById(refId);
	}

	@Override
	public DivisionInfo getDivisionInfo() {
		return this.divisionInfo;
	}

	@Override
	public SilkroadType getGameType() {
		return this.silkroadType ; 
	}

}
