package net.laboulangerie.laboulangeriemmo.core.abilities.thehunter;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Armadillo;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.CopperGolem;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Frog;
import org.bukkit.entity.Goat;
import org.bukkit.entity.HappyGhast;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Llama;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.Strider;
import org.bukkit.entity.TraderLlama;
import org.bukkit.entity.Vex;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.ZombieNautilus;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;

public class MobHeadsRegistry {

    private static final Map<String, List<HeadConfig>> headsByMob = new HashMap<>();
    private static final Set<String> DISABLED_MOB_HEADS = Set.of("SILVERFISH");
    private static final Random random = new Random();

    public static void loadHeads() {
        headsByMob.clear();

        File configFile = new File(LaBoulangerieMmo.PLUGIN.getDataFolder(), "mobs_head.yml");

        // Save default config if it doesn't exist
        if (!configFile.exists()) {
            LaBoulangerieMmo.PLUGIN.saveResource("mobs_head.yml", false);
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        // Load defaults from jar
        InputStream defConfigStream = LaBoulangerieMmo.PLUGIN.getResource("mobs_head.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream));
            config.setDefaults(defConfig);
        }

        ConfigurationSection headsSection = config.getConfigurationSection("heads");
        if (headsSection == null) {
            LaBoulangerieMmo.PLUGIN.getLogger().warning("No heads section found in mobs_head.yml");
            return;
        }

        for (String mobType : headsSection.getKeys(false)) {
            if (DISABLED_MOB_HEADS.contains(mobType.toUpperCase())) continue;

            List<Map<?, ?>> headList = headsSection.getMapList(mobType);
            List<HeadConfig> configs = new ArrayList<>();

            for (Map<?, ?> headData : headList) {
                String textureValue = (String) headData.get("texture");
                Object rarityObj = headData.get("rarity");
                String rarity = rarityObj != null ? (String) rarityObj : "common";
                String condition = (String) headData.get("condition");
                String displayName = (String) headData.get("name");
                String material = (String) headData.get("material");

                try {
                    configs.add(new HeadConfig(textureValue, rarity, condition, displayName, material));
                } catch (IllegalArgumentException e) {
                    LaBoulangerieMmo.PLUGIN.getLogger().warning(
                            "Ignoring invalid head entry for " + mobType + ": " + e.getMessage());
                }
            }

            headsByMob.put(mobType.toUpperCase(), configs);
        }

        LaBoulangerieMmo.PLUGIN.getLogger().info("Loaded " + headsByMob.size() + " mob head configurations");
    }

    public static HeadConfig getHead(LivingEntity entity) {
        return getHead(getVisualProfile(entity));
    }

    public static HeadConfig getHead(MobVisualProfile profile) {
        List<HeadConfig> heads = headsByMob.get(profile.mobType());

        if (heads == null || heads.isEmpty()) {
            return null;
        }

        // Get condition for entity variant
        String entityCondition = profile.condition();

        // Filter by condition if applicable
        List<HeadConfig> exactHeads = new ArrayList<>();
        List<HeadConfig> generalizedHeads = new ArrayList<>();
        List<HeadConfig> fallbackHeads = new ArrayList<>();
        for (HeadConfig head : heads) {
            if (head.getCondition() == null || head.getCondition().isEmpty()) {
                fallbackHeads.add(head);
            } else if (entityCondition != null && head.getCondition().equalsIgnoreCase(entityCondition)) {
                exactHeads.add(head);
            } else if (entityCondition != null && entityCondition.startsWith("BABY_")
                    && head.getCondition().equalsIgnoreCase("BABY")) {
                generalizedHeads.add(head);
            }
        }

        List<HeadConfig> validHeads = !exactHeads.isEmpty() ? exactHeads
                : !generalizedHeads.isEmpty() ? generalizedHeads : fallbackHeads;
        if (validHeads.isEmpty()) return null;

        // Return random head from valid ones
        return validHeads.get(random.nextInt(validHeads.size()));
    }

    public static MobVisualProfile getVisualProfile(LivingEntity entity) {
        return new MobVisualProfile(entity.getType().name(), getEntityCondition(entity));
    }

    @SuppressWarnings("deprecation")
    private static String getEntityCondition(LivingEntity entity) {
        String baseCondition = null;

        // === VARIANTES SPÉCIFIQUES ===

        // Pandas - phenotype. Brown and weak are only expressed when both genes match.
        if (entity instanceof Panda panda) {
            Panda.Gene gene = panda.getMainGene();
            if (gene.isRecessive() && gene != panda.getHiddenGene()) gene = Panda.Gene.NORMAL;
            if (!panda.isAdult()) return gene == Panda.Gene.BROWN ? "BABY_BROWN" : "BABY";
            return gene.name();
        }
        // Armadillos - babies use the same textures as adults.
        if (entity instanceof Armadillo armadillo) {
            return armadillo.getState() == Armadillo.State.SCARED ? "SCARED" : "NORMAL";
        }
        // Zombie nautilus - babies use the same texture as their biome variant.
        if (entity instanceof ZombieNautilus zombieNautilus) {
            return zombieNautilus.getVariant().getKey().getKey().toUpperCase();
        }
        // Trader llamas have dedicated textures but share Llama colors.
        if (entity instanceof TraderLlama traderLlama) {
            return traderLlama.getColor().name();
        }
        // Snow golems have different faces depending on whether their pumpkin was sheared.
        if (entity instanceof Snowman snowman) {
            return snowman.isDerp() ? "NO_PUMPKIN" : "PUMPKIN";
        }
        // Copper golems visually follow their four weathering states.
        if (entity instanceof CopperGolem copperGolem) {
            return copperGolem.getWeatheringState().name();
        }
        // Baby happy ghasts have their own head. Adults use the color of their body harness.
        if (entity instanceof HappyGhast happyGhast) {
            if (!happyGhast.isAdult()) return "BABY";

            ItemStack harness = happyGhast.getEquipment().getItem(EquipmentSlot.BODY);
            Material harnessType = harness.getType();
            String materialName = harnessType.name();
            if (materialName.endsWith("_HARNESS")) {
                return materialName.substring(0, materialName.length() - "_HARNESS".length());
            }
            return null;
        }

        // Moutons - couleur
        if (entity instanceof Sheep sheep) {
            baseCondition = sheep.getColor() != null ? sheep.getColor().name() : null;
        }
        // Farm animals - climate variant, combined with BABY below when needed.
        else if (entity instanceof Pig pig) {
            baseCondition = pig.getVariant().getKey().getKey().toUpperCase();
        }
        else if (entity instanceof Chicken chicken) {
            baseCondition = chicken.getVariant().getKey().getKey().toUpperCase();
        }
        // Champimeuh must be checked before Cow because MushroomCow extends Cow.
        else if (entity instanceof MushroomCow mooshroom) {
            baseCondition = mooshroom.getVariant().name();
        }
        else if (entity instanceof Cow cow) {
            baseCondition = cow.getVariant().getKey().getKey().toUpperCase();
        }
        // Vex - charging is the red/angry visual state.
        else if (entity instanceof Vex vex) {
            baseCondition = vex.isCharging() ? "ANGRY" : "NORMAL";
        }
        // Chats - type
        else if (entity instanceof Cat cat) {
            baseCondition = cat.getCatType().name();
        }
        // Loups - variant (1.20.5+)
        else if (entity instanceof Wolf wolf) {
            String variant = wolf.getVariant().getKey().getKey().toUpperCase();
            return wolfCondition(variant, wolf.isAdult(), wolf.isAngry());
        }
        // Grenouilles - variant
        else if (entity instanceof Frog frog) {
            baseCondition = frog.getVariant().name();
        }
        // Strider - froid
        else if (entity instanceof Strider strider) {
            baseCondition = strider.isShivering() ? "COLD" : null;
        }
        // Chèvre - hurlante
        else if (entity instanceof Goat goat) {
            baseCondition = goat.isScreaming() ? "SCREAMING" : null;
        }
        // Abeille - états combinés
        else if (entity instanceof Bee bee) {
            boolean angry = bee.getAnger() > 0;
            boolean nectar = bee.hasNectar();
            if (nectar && angry) baseCondition = "POLLINATED_ANGRY";
            else if (nectar) baseCondition = "POLLINATED";
            else if (angry) baseCondition = "ANGRY";
        }
        // Axolotl - variant
        else if (entity instanceof Axolotl axolotl) {
            baseCondition = axolotl.getVariant().name();
        }
        // Renard - type
        else if (entity instanceof Fox fox) {
            baseCondition = fox.getFoxType().name().equals("RED") ? null : "SNOW";
        }
        // Lapin - type
        else if (entity instanceof Rabbit rabbit) {
            baseCondition = rabbit.getRabbitType().name();
        }
        // Cheval - couleur
        else if (entity instanceof Horse horse) {
            baseCondition = horse.getColor().name();
        }
        // Llama - couleur
        else if (entity instanceof Llama llama) {
            baseCondition = llama.getColor().name();
        }
        // Parrot - variant
        else if (entity instanceof Parrot parrot) {
            baseCondition = parrot.getVariant().name();
        }

        // === GESTION BABY ===
        // Combiner avec BABY si c'est un bébé
        if (entity instanceof Ageable ageable && !ageable.isAdult()) {
            if (baseCondition != null) {
                return "BABY_" + baseCondition;
            }
            return "BABY";
        }

        return baseCondition;
    }

    static String wolfCondition(String variant, boolean adult, boolean angry) {
        if (!adult) return "BABY_" + variant;
        return angry ? "ANGRY_" + variant : variant;
    }

    public static boolean hasHead(String mobType) {
        return headsByMob.containsKey(mobType.toUpperCase());
    }
}
