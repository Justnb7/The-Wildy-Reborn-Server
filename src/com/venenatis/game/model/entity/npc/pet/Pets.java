package com.venenatis.game.model.entity.npc.pet;

import java.util.HashMap;
import java.util.Map;

//Enum data credits to Stan Jansen.
	public enum Pets {
      //item id, npc id
		ABYSSAL_ORPHAN(13262, 5883),
		BABY_MOLE(12646, 6635),
		CALLISTO_CUB(13178, 497),
		HELLPUPPY(13247, 964),
		CHAOS_ELEMENTAL(11995, 5907),
		DAGANNOTH_PRIME(12644, 6627),
		DAGANNOTH_REX(12645, 6630),
		DAGANNOTH_SUPREME(12643, 6626),
		DARK_CORE(12816, 388),
		GENERAL_GRAARDOR(12650, 6632),
		KRIL_TSUTSAROTH(12652, 6634),
		KRAKEN(12655, 6640),
		KREEARRA(12649, 6631),
		PENANCE_PET(12703, 6642),
		SMOKE_DEVIL(12648, 6639),
		ZILYANA(12651, 6633),
		SNAKELING(12921, 2130),
		SNAKELING_RED(12939, 2131),
		SNAKELING_BLUE(12940, 2132),
		PRINCE_BLACK_DRAGON(12653, 6636),
		SCORPIAS_OFFSPRING(13181, 5547),
		TZREK_JAD(13225, 5892),
		VENENATIS_SPIDERLING(13177, 5557),
		VETION_PURPLE(13179, 5559),
		VETION_ORANGE(13180, 5560),
		KALPHITE_PRINCESS_BUG(12647, 6638),
		KALPHITE_PRINCESS_FLY(12654, 6637),
		HERON(13320, 6715),
		ROCK_GOLEM(13321, 7439),
		ROCK_GOLEM_TIN(21187, 7440),
		ROCK_GOLEM_COPPER(21188, 7441),
		ROCK_GOLEM_IRON(21189, 7442),
		ROCK_GOLEM_BLURITE(21190, 7443),
		ROCK_GOLEM_SILVER(21191, 7444),
		ROCK_GOLEM_COAL(21192, 7445),
		ROCK_GOLEM_GOLD(21193, 7446),
		ROCK_GOLEM_MITHRIL(21194, 7447),
		ROCK_GOLEM_GRANITE(21195, 7448),
		ROCK_GOLEM_ADAMANTITE(21196, 7449),
		ROCK_GOLEM_RUNITE(21197, 7450),
		BEAVER(13322, 6717),
		GIANT_SQUIRREL(20659,7334),
		ROCKY(20663, 7336),
		TANGLEROOT(20661, 7335),
		RIFT_GUARDIAN_FIRE(20665, 7337),
		RIFT_GUARDIAN_AIR(20667, 7337),
		RIFT_GUARDIAN_MIND(20669, 7337),
		RIFT_GUARDIAN_WATER(20671, 7337),
		RIFT_GUARDIAN_EARTH(20673, 7337),
		RIFT_GUARDIAN_CHAOS(20675, 7337),
		RIFT_GUARDIAN_BODY(20677, 7337),
		RIFT_GUARDIAN_COSMIC(20679, 7337),
		RIFT_GUARDIAN_NATURE(20681, 7337),
		RIFT_GUARDIAN_LAW(20683, 7337),
		RIFT_GUARDIAN_DEATH(20685, 7337),
		RIFT_GUARDIAN_SOUL(20687, 7337),
		RIFT_GUARDIAN_ASTRAL(20689, 7337),
		RIFT_GUARDIAN_BLOOD(20691, 7337),
		BABY_CHINCHOMPA(13323, 6756),
		BABY_CHINCHOMPA_GREY(13324, 6757),
		BABY_CHINCHOMPA_BLACK(13325, 6758),
		BABY_CHINCHOMPA_GOLD(13326, 6759),
		CHOMPY_CHICK(13071, 4001),
		BLOODHOUND(19730, 7232),
		PHOENIX(20693, 7368),
		OLMLET(20851, 7519),
		ZOMBIES_CHAMPION(22010, 8000),
		DAGANNOTH_MOTHER_MELEE(22011, 8001),
		DAGANNOTH_MOTHER_RANGE(22011, 8002),
		DAGANNOTH_MOTHER_MAGIC(22011, 8003),
		DARK_BEAST(22013, 8004),
		FLAMBEED(22014, 8005),
		DEATH_JR(22015, 8006);

		/**
		 * The pet item
		 */
      private final int item;
      
      /**
       * The pet
       */
      private final int npc;

      /**
       * Constructs an pet
       * @param item
       *         The itemId
       * @param npc
       *         The npcId
       */
      Pets(int item, int npc) {
          this.item = item;
          this.npc = npc;
      }

      /**
       * We store all our pet items in a map so we can access them later.
       */
      private static Map<Integer, Pets> petItems = new HashMap<Integer, Pets>();
      
      /**
       * We're also storing our pet npcs in a map so we can access these later too.
       */
      private static Map<Integer, Pets> petNpcs = new HashMap<Integer, Pets>();

      /**
       * Get the pet by itemId
       * @param item
       *         The itemId
       * @return The pet item
       */
      public static Pets from(int item) {
          return petItems.get(item);
      }

      /**
       * Grabs the pet by npcId
       * @param npc
       *         The npc Id
       * @return The pet npc
       */
      public static Pets fromNpc(int npc) {
          return petNpcs.get(npc);
      }

      static {
          for (Pets pet : Pets.values()) {
              petItems.put(pet.item, pet);
          }
          for (Pets pet : Pets.values()) {
              petNpcs.put(pet.npc, pet);
          }
      }

      /**
       * An public getter for the pet item
       * @return
       */
      public int getItem() {
          return item;
      }

      /**
       * A public getter for the pet npc
       * @return
       */
      public int getNpc() {
          return npc;
      }
      
  }