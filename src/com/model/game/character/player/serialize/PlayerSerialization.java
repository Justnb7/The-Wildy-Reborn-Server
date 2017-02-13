package com.model.game.character.player.serialize;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.model.game.World;
import com.model.game.character.combat.magic.SpellBook;
import com.model.game.character.npc.BossDeathTracker.BossName;
import com.model.game.character.npc.SlayerDeathTracker.SlayerNpcName;
import com.model.game.character.player.Player;
import com.model.game.character.player.Rights;
import com.model.game.character.player.Skills;
import com.model.game.character.player.content.bounty_hunter.BountyHunterConstants;
import com.model.game.item.Item;
import com.model.game.item.bank.BankItem;
import com.model.utility.Utility;
import com.model.utility.logging.PlayerLogging;
import com.model.utility.logging.PlayerLogging.LogType;



/**
 * Handles all player serialization
 *
 * @author Sanity
 * @author Mobster
 * @author Patrick van Elderen
 */
public class PlayerSerialization {

    /**
     * An executor service which saves accounts on its own thread
     */
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    public static int loadGame(Player p, String playerName, String playerPass) {
        return loadGame(p, playerName, playerPass, false);
    }
    
    /**
	 * The log directory which we can use to log data
	 */
	public static final File CHARACTER_DIRECTORY = new File("./Data/characters/");
	
	/**
	 * Gets the directory for the characters
	 * 
	 * @return The directory for the characters
	 */
	public static String getCharacterDirectory() {
		return CHARACTER_DIRECTORY.getPath();
	}
	
    /**
     * Loading
     *
     * @throws IOException
     */
	public static int loadGame(Player p, String playerName, String playerPass, boolean withoutPass) {
		playerName = playerName.toLowerCase();

		File file = new File(getCharacterDirectory(), playerName + ".txt");
		if (!file.exists()) {
			Utility.println(playerName + ": character file not found.");
			p.newPlayer = false;
			return 0;
		}
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			int mode = 0;
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				int spot = line.indexOf("=");
				if (spot > -1) {

					String key = line.substring(0, spot).trim();
					String value = line.substring(spot + 1).trim();
					String[] values = value.split("\t");

					
					switch (mode) {
					
					//Account
					case 1:
						if (key.equals("character-password")) {
                            p.passHash = value;
                            if (!withoutPass) {
                                if (!Utility.md5Hash(playerPass).equals(value)) {
                                    return 3;
                                }
                            }
						} else if (key.equals("character-rights")) {
							Rights right = Rights.get(Integer.parseInt(value));
							p.setRights(right);
						} else if (key.equals("character-identity")) {
							if (!p.getAttribute("identity", "").equalsIgnoreCase(value)) {
								PlayerLogging.write(LogType.CHANGE_IDENTITY, p, "Identity Changed: previous = " + value + ", new = " + p.getAttribute("identity", ""));
							}
							p.setIdentity(p.getAttribute("character-identity", ""));
						} else if (key.equals("character-mac-address")) {
							if (!p.getAttribute("mac-address", "").equalsIgnoreCase(value)) {
								PlayerLogging.write(LogType.CHANGE_MAC_ADDRESS, p, "Mac Address Changed: previous = " + value + ", new = " + p.getAttribute("mac-address", ""));
							}
							p.setMacAddress(p.getAttribute("character-mac-address", ""));
						} else if (key.equals("character-account-tutorial")) {
							p.setTutorial(Boolean.parseBoolean(value));
						} else if (key.equals("character-received-starter")) {
							p.setReceivedStarter(Boolean.parseBoolean(value));
						} else if (key.equals("character-account-type")) {
							p.setGameMode(value);
						} else if (key.equals("character-bank-pin")) {
                            p.getBankPin().setPin(value);
						} else if (key.equals("character-posx")) {
							p.teleportToX = (Integer.parseInt(value) <= 0 ? 3091 : Integer.parseInt(value));
						} else if (key.equals("character-posy")) {
							p.teleportToY = (Integer.parseInt(value) <= 0 ? 3502 : Integer.parseInt(value));
						} else if (key.equals("character-height")) {
							p.teleHeight = Integer.parseInt(value);
    					} else if (key.equals("character-special-amount")) {
                            p.setSpecialAmount(Integer.parseInt(value));
    					} else if (key.equals("character-was-reseted")) {
    						p.ecoReset = Boolean.parseBoolean(value);
    					}
						break;
						
					//Character	
					case 2:
						if (key.equals("magic-book")) {
							// read right book
							for( SpellBook b : SpellBook.values()) {
								if (value.equalsIgnoreCase(b.name())) {
									p.setSpellBook(b);
									break;
								}
							}
						} else if (key.equals("exp-counter")) {
							p.getSkills().setExpCounter(Integer.parseInt(value));
						} else if (key.equals("recoil")) {
							p.setRecoil(Integer.parseInt(value));
						} else if (key.equals("ringOfSuffering")) {
							p.setROSuffering(Integer.parseInt(value));
						} else if (key.equals("votePoints")) {
							p.setVotePoints(Integer.parseInt(value));
                        } else if (key.equals("totalVotes")) {
                        	p.setTotalVotes(Integer.parseInt(value));
                        } else if (key.equals("pkPoints")) {
                        	p.setPkPoints(Integer.parseInt(value));
                        } else if (key.equals("triviaPoints")) {
                        	p.setTriviaPoints(Integer.parseInt(value));
                        } else if (key.equals("dp")) {
                        	p.setAmountDonated(Integer.parseInt(value));
                        } else if (key.equals("alldp")) {
                        	p.setTotalAmountDonated(Integer.parseInt(value));
                        } else if (key.equals("teleblock-length")) {
                            p.teleblock.reset();
                            p.teleblockLength = Integer.parseInt(value);
                        } else if (key.equals("muted")) {
    						p.isMuted = Boolean.parseBoolean(value);
                        } else if (key.equals("yellMute")) {
    						p.yellMute = Integer.parseInt(value);
                        } else if (key.equals("skull-timer")) {
                            p.skullTimer = Integer.parseInt(value);
						} else if (key.equals("infection-type")) {
                            p.infection = Integer.parseInt(value);
						} else if (key.equals("slayer-task")) {
                            p.setSlayerTask(Integer.parseInt(value));
                        } else if (key.equals("task-amount")) {
                        	p.setSlayerTaskAmount(Integer.parseInt(value));
                        } else if (key.equals("lastClanChat")) {
                            p.setTempKey(value);
                        } else if (key.equals("clanChatPunishment")) {
                            p.isClanMuted = Boolean.parseBoolean(value);
						} else if(key.equals("gear-points")) {
							p.setGearPoints(Integer.parseInt(value));
						} else if(key.equals("max-cape-claimed")) {
							p.setClaimedMaxCape(Boolean.parseBoolean(value));
						}
						break;
					
					// Equipment
					case 3:
						if (key.equals("character-equip")) {
							p.playerEquipment[Integer.parseInt(values[0])] = Integer.parseInt(values[1]);
							p.playerEquipmentN[Integer.parseInt(values[0])] = Integer.parseInt(values[2]);
						}
						break;

					// Look
					case 4:
						if (key.equals("character-look")) {
							p.playerAppearance[Integer.parseInt(values[0])] = Integer.parseInt(values[1]);
						}
						break;

					// Skills
					case 5:
						if (key.equals("character-skill")) {
							p.getSkills().getAllDynamicLevels()[Integer.parseInt(values[0])] = Integer.parseInt(values[1]);
							p.getSkills().getAllXP()[Integer.parseInt(values[0])] = Integer.parseInt(values[2]);
						}
						break;

					// Inventory Items
					case 6:
						if (key.equals("character-item")) {
							p.playerItems[Integer.parseInt(values[0])] = Integer.parseInt(values[1]);
							p.playerItemsN[Integer.parseInt(values[0])] = Integer.parseInt(values[2]);
						}
						break;

					// Bank
					case 7:
						if (key.equals("character-bank")) {
							p.bankItems[Integer.parseInt(values[0])] = Integer.parseInt(values[1]);
							p.bankItemsN[Integer.parseInt(values[0])] = Integer.parseInt(values[2]);
							p.getBank().getBankTab()[0].add(new BankItem(Integer.parseInt(values[1]), Integer.parseInt(values[2])));
						} else if (key.equals("bank-tab")) {
							int tabId = Integer.parseInt(values[0]);
							int itemId = Integer.parseInt(values[1]);
							int itemAmount = Integer.parseInt(values[2]);
							p.getBank().getBankTab()[tabId].add(new BankItem(itemId, itemAmount));
						}
						break;
						
					// Character Friends
					case 8:
						if (key.equals("friends")) {
                            p.getFAI().getFriendsList().add(Long.parseLong(values[0]));
                        }
						break;
					
					// Character Ignores
					case 9:
						if (key.equals("ignores")) {
                            for (int i = 0; i < values.length; i++) {
                                p.getFAI().getIgnoreList().add(Long.parseLong(values[i]));
                            }
                        }
						break;
						
					// Character Killstreaks
					case 10:
						if (line.startsWith("currentKillStreak")) {
							p.setCurrentKillStreak(Integer.parseInt(value));
						} else if (line.startsWith("highestKillStreak")) {
							p.setHighestKillStreak(Integer.parseInt(value));
						} else if (line.startsWith("wildernessKillStreak")) {
							p.setWildernessKillStreak(Integer.parseInt(value));
						}
						break;
					
					// Character Wilderness
					case 11:
						if (key.startsWith("last-killed")) {
							p.lastKilledList.add(value);
						} else if (line.startsWith("killCount")) {
							p.setKillCount(Integer.parseInt(value));
						} else if (line.startsWith("deathCount")) {
							p.setDeathCount(Integer.parseInt(value));
						}
						break;
						
					// Bounty Hunter
					case 12:
						if (key.equals("rogue-current")) {
                            p.setAttribute(BountyHunterConstants.ROGUE_CURRENT, Integer.parseInt(value));
                        } else if (key.equals("rogue-record")) {
                            p.setAttribute(BountyHunterConstants.ROGUE_RECORD, Integer.parseInt(value));
                        } else if (key.equals("hunter-current")) {
                            p.setAttribute(BountyHunterConstants.HUNTER_CURRENT, Integer.parseInt(value));
                        } else if (key.equals("hunter-record")) {
                            p.setAttribute(BountyHunterConstants.HUNTER_RECORD, Integer.parseInt(value));
                        } else if (key.equals("bounty-points")) {
                            p.setBountyPoints(Integer.parseInt(value));
                        }
						break;
					
					// Boss Tracker
					case 13:
						BossName name = BossName.get(key);
						if (name != null) {
							p.getBossDeathTracker().getTracker().put(name, Integer.parseInt(value));
						}
                        break;
                        
                    // Slayer Tracker 
					case 14:
						SlayerNpcName names = SlayerNpcName.get(key);
                        if (names != null) {
                            p.getSlayerDeathTracker().getTracker().put(names, Integer.parseInt(value));
                        }
						break;
						
					// Pets
					case 15:
						if (key.equals("petId")) {
    						p.petId = Integer.parseInt(value);
                    	}
						break;
						
					// Achievements Tier 1
					case 16:
                        
                        break;
                        
                    // Achievements Tier 2
                    case 17:
                        
                        break;
                        
                    // Achievements Tier 3
                    case 18:
                        
                        break;
                     
                    // Charges
					case 19:
						
						break;
                    
                    // Titles
                    case 20:
                    	
                    	break;
                    	
                    // Settings
                    case 21:
                    	if (key.equals("isRunning")) {
                    		p.setRunning(Boolean.parseBoolean(value));
                        } else if (key.equals("autoRetaliate")) {
                            p.setAutoRetaliating(Boolean.parseBoolean(value));
                        } else if (key.equals("attackStyle")) {
                        	p.setAttackStyle(Integer.parseInt(value));
                        } else if (key.equals("attackStyleConfig")) {
                        	p.setAttackStyleConfig(Integer.parseInt(value));
                        } else if (key.equals("brightness")) {
                            p.setScreenBrightness((byte) Integer.parseInt(value));
                        } else if (key.equals("splitprivatechat")) {
                            p.setSplitPrivateChat(Boolean.parseBoolean(value));
                        } else if (key.equals("music-player")) {
                            p.setEnableMusic(Boolean.parseBoolean(value));
                        } else if (key.equals("sounds-player")) {
                            p.setEnableSound(Boolean.parseBoolean(value));
                        } else if (key.equals("music_volume")) {
                            p.setAttribute("music_volume", Integer.parseInt(value));
                        } else if (key.equals("sound_volume")) {
                            p.setAttribute("sound_volume", Integer.parseInt(value));
                        }
                    	break;
                    	
                    case 22:
                    	if(key.equals("item-set")) {
                    		p.getRunePouchContainer().add(new Item(Integer.parseInt(values[1]), Integer.parseInt(values[2])));
                    	}
                    	break;
                    	
                    case 23:
                    	if (key.equals("death-shop-chat")) {
                            p.deathShopChat = Boolean.parseBoolean(value);
                        } else if (key.equals("death-shop-items")) {
                            p.deathShop.getContainer().container()[Integer.parseInt(values[0])] = new Item(Integer.parseInt(values[1]), Integer.parseInt(values[2]));
                        }
                    	break;
						
					}
				} else {
					if (line.equals("[CHARACTER-ACCOUNT]")) {
						mode = 1;
					} else if (line.equals("[CHARACTER]")) {
						mode = 2;
					} else if (line.equals("[CHARACTER-EQUIPMENT]")) {
						mode = 3;
					} else if (line.equals("[CHARACTER-LOOK]")) {
						mode = 4;
					} else if (line.equals("[CHARACTER-SKILLS]")) {
						mode = 5;
					} else if (line.equals("[CHARACTER-ITEMS]")) {
						mode = 6;
					} else if (line.equals("[CHARACTER-BANK]")) {
						mode = 7;
					} else if (line.equals("[CHARACTER-FRIENDS]")) {
                        mode = 8;
                    } else if (line.equals("[CHARACTER-IGNORES]")) {
                        mode = 9;
                    } else if (line.equals("[CHARACTER-KILLSTREAKS]")) {
                        mode = 10;
                    } else if (line.equals("[CHARACTER-KILLS]")) {
                        mode = 11;
                    } else if (line.equals("[BOUNTY-HUNTER]")) {
                        mode = 12;
                    } else if (line.equals("[BOSS-TRACKER]")) {
                        mode = 13;
                    } else if (line.equals("[SLAYER-TRACKER]")) {
                        mode = 14;
                    } else if (line.equals("[CHARACTER-PETS]")) {
                        mode = 15;
                    } else if (line.equals("[ACHIEVEMENTS-TIER-1]")) {
                        mode = 16;
                    } else if (line.equals("[ACHIEVEMENTS-TIER-2]")) {
                        mode = 17;
                    } else if (line.equals("[ACHIEVEMENTS-TIER-3]")) {
                        mode = 18;
                    } else if (line.equals("[CHARACTER-CHARGES]")) {
    					mode = 19;
                    } else if (line.equals("[CHARACTER-TITLES]")) {
    					mode = 20;
                    } else if (line.equals("[CHARACTER-SETTINGS]")) {
    					mode = 21;
                    } else if (line.equals("[CHARACTER-RUNE-POUCH]")) {
                    	mode = 22;
                    } else if (line.equals("[TASKS-DIFFICULTY-EASY]")) {
                        mode = 23;
                    } else if (line.equals("[TASKS-DIFFICULTY-MEDIUM]")) {
                        mode = 24;
                    } else if (line.equals("[TASKS-DIFFICULTY-HARD]")) {
                        mode = 25;
                    } else if (line.equals("[TASKS-DIFFICULTY-ELITE]")) {
                        mode = 26;
                    }
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 13;
	}

    /**
     * Saves the account for a player
     *
     * @param player
     *            The {@link Player to save the account for
     */
    public static void saveGame(final Player player) {
        if (!player.saveFile || player.newPlayer || !player.saveCharacter) {
            return;
        }
        if (player.getName() == null || World.getWorld().getPlayers().get(player.getIndex()) == null) {
            return;
        }
        writeData(player);
    }
    
    public static String quick = "";

    /**
     * Writes data to the character file
     *
     * @param p
     *            The {@link Player} to write the data for
     */
    public static void writeData(Player p) {
        String username = p.getRealUsername().toLowerCase();
        final int time = p.teleblock.isStopped() ? 0 : (int) (p.teleblockLength - p.teleblock.elapsedTime());
        final int tbTime = time > 300000 || time < 0 ? 0 : time;
        //spellBook = p.getSpellBook();
        File file = new File(getCharacterDirectory(), username + ".txt");
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
        	
        	/* Character Account */
        	
            writer.write("[CHARACTER-ACCOUNT]");
            writer.newLine();
            writer.write("character-password = ");
            String passToWrite = p.getPassword() == null || p.getPassword().length() == 0 ? p.passHash : Utility.md5Hash(p.getPassword());
            writer.write(passToWrite);
            writer.newLine();
            writer.write("character-rights = " + p.getRights().getValue());
            writer.newLine();
            writer.write("character-identity = " + p.getIdentity());
            writer.newLine();
            writer.write("character-mac-address = " + p.getMacAddress());
            writer.newLine();
            writer.write("character-account-tutorial = ");
            writer.write(Boolean.toString(p.inTutorial()));
            writer.newLine();
            writer.write("character-received-starter = ");
            writer.write(Boolean.toString(p.receivedStarter()));
            writer.newLine();
            writer.write("character-account-type = " + p.getGameMode());
            writer.newLine();
            writer.write("character-bank-pin = " + p.getBankPin().getPin());
            writer.newLine();
            writer.write("character-posx = ");
            writer.write(Integer.toString(p.getX()));
            writer.newLine();
            writer.write("character-posy = ");
            writer.write(Integer.toString(p.getY()));
            writer.newLine();
            writer.write("character-height = ");
            writer.write(Integer.toString(p.heightLevel));
            writer.newLine();
	        writer.write("character-special-amount = ");
	        writer.write(Integer.toString(p.getSpecialAmount()));
	        writer.newLine();
	        writer.write("character-was-reseted = ");
	        writer.write(Boolean.toString(p.ecoReset));
	        writer.newLine();
			writer.newLine();
			
			/* Character */
			
			writer.write("[CHARACTER]");
			writer.newLine();
            writer.write("magic-book = "+p.getSpellBook().toString());
            writer.newLine();
            writer.write("exp-counter = " + p.getSkills().getExpCounter());
            writer.newLine();
            writer.write("recoil = " + p.getRecoil());
            writer.newLine();
            writer.write("ringOfSuffering = " + p.getROSuffering());
            writer.newLine();
            writer.write("votePoints = " + p.getVotePoints());
            writer.newLine();
            writer.write("totalVotes = " + p.getTotalVotes());
            writer.newLine();
            writer.write("pkPoints = " + p.getPkPoints());
            writer.newLine();
            writer.write("triviaPoints = ");
            writer.write(Integer.toString(p.getTriviaPoints()));
            writer.newLine();
            writer.write("dp = " + p.getAmountDonated());
            writer.newLine();
            writer.write("alldp = " + p.getTotalAmountDonated());
            writer.newLine();
            writer.write("teleblock-length = ");
            writer.write(Integer.toString(tbTime));
            writer.newLine();
            writer.write("muted = ");
            writer.write(Boolean.toString(p.isMuted), 0, Boolean.toString(p.isMuted).length());
            writer.newLine();
            writer.write("yellMute = ");
            writer.write(Integer.toString(p.yellMute));
            writer.newLine();
            writer.write("skull-timer = ");
            writer.write(Integer.toString(p.skullTimer));
            writer.newLine();
            writer.write("infection-type = ");
            writer.write(Integer.toString(p.infection));
            writer.newLine();
			writer.write("slayer-task = ");
            writer.write(Integer.toString(p.getSlayerTask()));
            writer.newLine();
            writer.write("task-amount = ");
            writer.write(Integer.toString(p.getSlayerTaskAmount()));
            writer.newLine();
            writer.write("lastClanChat = ");
            writer.write(p.getClanMembership() == null ? "" : p.getClanMembership().getClanOwner());
            writer.newLine();
			writer.write("clanChatPunishment = " + p.getClanPunishment());
            writer.newLine();
            writer.write("gear-points = " + p.getGearPoints());
			writer.newLine();
			writer.write("max-cape-claimed = " + p.hasClaimedMax());
			writer.newLine();
			writer.newLine();
            
            /* EQUIPMENT */
            writer.write("[CHARACTER-EQUIPMENT]");
            writer.newLine();
            for (int i = 0; i < p.playerEquipment.length; i++) {
                writer.write("character-equip = ", 0, 18);
                writer.write(Integer.toString(i), 0, Integer.toString(i).length());
                writer.write("	", 0, 1);
                writer.write(Integer.toString(p.playerEquipment[i]), 0, Integer.toString(p.playerEquipment[i]).length());
                writer.write("	", 0, 1);
                writer.write(Integer.toString(p.playerEquipmentN[i]), 0, Integer.toString(p.playerEquipmentN[i]).length());
                writer.write("	", 0, 1);
                writer.newLine();
            }
            writer.newLine();
            writer.newLine();

            /* LOOK */
            writer.write("[CHARACTER-LOOK]");
            writer.newLine();
            for (int i = 0; i < p.playerAppearance.length; i++) {
                writer.write("character-look = ", 0, 17);
                writer.write(Integer.toString(i), 0, Integer.toString(i).length());
                writer.write("	", 0, 1);
                writer.write(Integer.toString(p.playerAppearance[i]), 0, Integer.toString(p.playerAppearance[i]).length());
                writer.newLine();
            }
            writer.newLine();
            writer.newLine();

            /* SKILLS */
            writer.write("[CHARACTER-SKILLS]");
            writer.newLine();
            for (int i = 0; i < Skills.SKILL_COUNT; i++) {
                writer.write("character-skill = ", 0, 18);
                writer.write(Integer.toString(i), 0, Integer.toString(i).length());
                writer.write("	", 0, 1);
                writer.write(Integer.toString(p.getSkills().getLevel(i)), 0, Integer.toString(p.getSkills().getLevel(i)).length());
                writer.write("	", 0, 1);
                writer.write(Integer.toString((int) p.getSkills().getExperience(i)), 0, Integer.toString((int) p.getSkills().getExperience(i)).length());
                writer.newLine();
            }
            writer.newLine();
            writer.newLine();

            /* ITEMS */
            writer.write("[CHARACTER-ITEMS]");
            writer.newLine();
            for (int i = 0; i < p.playerItems.length; i++) {
                if (p.playerItems[i] > 0) {
                    writer.write("character-item = ", 0, 17);
                    writer.write(Integer.toString(i), 0, Integer.toString(i).length());
                    writer.write("	", 0, 1);
                    writer.write(Integer.toString(p.playerItems[i]), 0, Integer.toString(p.playerItems[i]).length());
                    writer.write("	", 0, 1);
                    writer.write(Integer.toString(p.playerItemsN[i]), 0, Integer.toString(p.playerItemsN[i]).length());
                    writer.newLine();
                }
            }
            writer.newLine();
            writer.newLine();

            /* BANK */
            writer.write("[CHARACTER-BANK]");
            writer.newLine();
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < p.BANK_SIZE; j++) {
                    if (j > p.getBank().getBankTab()[i].size() - 1)
                        break;
                    BankItem item = p.getBank().getBankTab()[i].getItem(j);
                    if (item == null)
                        continue;
                    writer.write("bank-tab = " + i + "\t" + item.getId() + "\t" + item.getAmount());
                    writer.newLine();
                }
            }

            writer.newLine();
            writer.newLine();
            
            /* FRIENDS */
            writer.write("[CHARACTER-FRIENDS]");
            writer.newLine();
            for (Long l : p.getFAI().getFriendsList()) {
                writer.write("friends = ");
                writer.write(l + "\t");
                writer.newLine();
            }
            writer.newLine();
            writer.newLine();
            
            /* IGNORES */
            writer.write("[CHARACTER-IGNORES]");
            writer.newLine();
            for (Long l : p.getFAI().getIgnoreList()) {
                writer.write("ignores = ");
                writer.write(l + "\t");
                writer.newLine();
            }
			writer.newLine();
			writer.newLine();
			
			/* Killstreaks */
            writer.write("[CHARACTER-KILLSTREAKS]");
            writer.newLine();
            writer.write("currentKillStreak = " + p.getCurrentKillStreak());
            writer.newLine();
            writer.write("highestKillStreak = " + p.getHighestKillStreak());
            writer.newLine();
            writer.write("wildernessKillStreak = " + p.getWildernessKillStreak());
            writer.newLine();
            writer.newLine();
            
            /* Kills */
            writer.write("[CHARACTER-KILLS]");
            writer.newLine();
            for (int i = 0; i < p.lastKilledList.size(); i++) {
                if (p.lastKilledList.get(i) != null && !p.lastKilledList.get(i).equalsIgnoreCase("null")) {
                    writer.write("last-killed = " + p.lastKilledList.get(i));
                    writer.newLine();
                }
            }
            writer.newLine();
            writer.write("killCount = " + p.getKillCount());
            writer.newLine();
            writer.write("deathCount = " + p.getDeathCount());
            writer.newLine();
            writer.newLine();
            
            /* Bounty Hunter */
            writer.write("[BOUNTY-HUNTER]");
            writer.newLine();
            writer.write("rogue-current = " + p.getAttribute(BountyHunterConstants.ROGUE_CURRENT, 0));
            writer.newLine();
            writer.write("rogue-record = " + p.getAttribute(BountyHunterConstants.ROGUE_RECORD, 0));
            writer.newLine();
            writer.write("hunter-current = " + p.getAttribute(BountyHunterConstants.HUNTER_CURRENT, 0));
            writer.newLine();
            writer.write("hunter-record = " + p.getAttribute(BountyHunterConstants.HUNTER_RECORD, 0));
            writer.newLine();
            writer.write("bounty-points = " + p.getBountyPoints());
			writer.newLine();
			writer.newLine();
			
			/* Boss Tracker */
			writer.write("[BOSS-TRACKER]");
			writer.newLine();
			for (Entry<BossName, Integer> entry : p.getBossDeathTracker().getTracker().entrySet()) {
				if (entry != null) {
					if (entry.getValue() > 0) {
						writer.write(entry.getKey().toString() + " = " + entry.getValue());
						writer.newLine();
					}
				}
			}
			writer.newLine();
			writer.newLine();

			/* Slayer Tracker */
			writer.write("[SLAYER-TRACKER]");
			writer.newLine();
			for (Entry<SlayerNpcName, Integer> entrys : p.getSlayerDeathTracker().getTracker().entrySet()) {
				if (entrys != null) {
					if (entrys.getValue() > 0) {
						writer.write(entrys.getKey().toString() + " = " + entrys.getValue());
						writer.newLine();
					}
				}
			}
			writer.newLine();
			writer.newLine();

			/* Pets */
			writer.write("[CHARACTER-PETS]");
			writer.newLine();
			writer.write("petId = ", 0, 8);
			writer.write(Integer.toString(p.petId), 0, Integer.toString(p.petId).length());

			writer.newLine();
			writer.newLine();

			/* Achievements */

			writer.write("[ACHIEVEMENTS-TIER-1]");
			writer.newLine();
			writer.write("[ACHIEVEMENTS-TIER-2]");
			writer.newLine();
			writer.write("[ACHIEVEMENTS-TIER-3]");

			writer.newLine();
			writer.newLine();

			/* Titles */
			writer.write("[CHARACTER-TITLES]");
			writer.newLine();
			writer.write("character-title = " + p.getCurrentTitle());
			writer.newLine();
			writer.write("character-title-color = " + p.getCurrentTitleColor());
			writer.newLine();
			writer.newLine();

			/* Settings */
			writer.write("[CHARACTER-SETTINGS]");
			writer.newLine();
			writer.write("isRunning = " + p.isRunning());
			writer.newLine();
			writer.write("autoRetaliate = ");
			writer.write(Boolean.toString(p.isAutoRetaliating()));
			writer.newLine();
			writer.write("attackStyle = " + p.getAttackStyle());
			writer.newLine();
			writer.write("attackStyleConfig = " + p.getAttackStyleConfig());
			writer.newLine();
			writer.write("brightness = " + p.getScreenBrightness());
			writer.newLine();
			writer.write("splitprivatechat = " + p.getSplitPrivateChat());
			writer.newLine();
			writer.write("music-player = " + p.isEnableMusic());
			writer.newLine();
			writer.write("sounds-player = " + p.isEnableSound());
			writer.newLine();
			writer.write("music_volume = " + p.getAttribute("music_volume", 3));
            writer.newLine();
            writer.write("sound_volume = " + p.getAttribute("sound_volume", 3));
            writer.newLine();
            writer.write("music-unlocked = ");
            writer.newLine();
			writer.newLine();

			/* RUNE-POUCH */
			writer.write("[CHARACTER-RUNE-POUCH]");
			writer.newLine();
			for (int i = 0; i < p.getRunePouchContainer().size(); i++) {
				writer.write("item-set = ");
				writer.write(Integer.toString(i));// slot
				writer.write("	");
				writer.write(Integer.toString(p.getRunePouchContainer().getId(i)));// id
				writer.write("	");
				writer.write(Integer.toString(p.getRunePouchContainer().get(i).getAmount()));
				writer.newLine();
			}
			writer.newLine();
			writer.newLine();
			
			/* Death Shop */
			writer.write("[CHARACTER-DEATH-SHOP]");
			writer.newLine();
			writer.write("death-shop-chat = ");
            writer.write(Boolean.toString(p.deathShopChat));
            writer.newLine();
            for (int i = 0; i < p.deathShop.getContainer().container().length; i++) {
                if (p.deathShop.getContainer().container()[i] == null)
                    continue;
                writer.write("death-shop-items = ");
                writer.write(Integer.toString(i));
                writer.write("\t");
                writer.write(Integer.toString(p.deathShop.getContainer().container()[i].id));
                writer.write("\t");
                writer.write(Integer.toString(p.deathShop.getContainer().container()[i].amount));
                writer.write("\t");
            }
			writer.newLine();
            writer.newLine();
	            
			
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    public static boolean passwordMatches(String name, String password) throws IOException {
        if (!playerExists(name)) {
            return false;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(new File("./data/characters/" + name + ".txt")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                int spot = line.indexOf("=");
                if (spot > -1) {
                    String key = line.substring(0, spot).trim();
                    String value = line.substring(spot + 1).trim();
                    if (key != null && value != null) {
                        if (key.equals("character-password")) {
                            reader.close();
                            return password.equalsIgnoreCase(value) || Utility.basicEncrypt(password).equals(value) || Utility.md5Hash(password)
                                .equals(value);
                        }
                    }
                }
            }
            reader.close();
        }
        return false;
    }

    public static boolean playerExists(String name) {
        File file = null;
        file = new File("./data/characters/" + name + ".txt");
        return file != null && file.exists();
    }

	public static ExecutorService getExecutor() {
		return executor;
	}
}