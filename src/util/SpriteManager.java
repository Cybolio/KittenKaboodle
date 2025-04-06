package util;

import Main.GamePanel;
import entity.npc.Panda;

import java.util.ArrayList;

public class SpriteManager {
    private GamePanel gp;
    public ArrayList<StaticSprite> staticSprites = new ArrayList<>();
    public ArrayList<EntitySprite> entity = new ArrayList<>();

    public SpriteManager(GamePanel gp) {
        this.gp = gp;
        setupStaticSprites();
    }

    private void setupStaticSprites() {
        int tileSize = gp.getTileSize();

        // Buildings
        staticSprites.add(new StaticSprite(
                gp,
                (tileSize * 10) + 150, (tileSize * 10) - 70,
                "/tiles/Buildings/House5.png",
                4.0f, true,12, 3, 0.0, 75,
                "RIZZLY's Home", "RIZZLY's home"
        ));

        staticSprites.add(new StaticSprite(
                gp,
                (tileSize * 10) - 420, (tileSize * 10) - 70,
                "/tiles/Buildings/House2.png",
                4.0f, true, 12, 3, 0.0, 75,
                "Neighbor's Home", "Neighbor's home"
        ));

        staticSprites.add(new StaticSprite(
                gp,
                (tileSize * 10) + 150, (tileSize * 10) + 500,
                "/tiles/Buildings/House1.png",
                4.0f, true, 12, 3, 0.0, 75,
                "Your Home", "Your home"
        ));

        staticSprites.add(new StaticSprite(
                gp, (tileSize * 10) - 420, (tileSize * 10) + 500, "/tiles/Buildings/House4.png", 4.0f,
                true, 10, 5, 0.0, 40, "Panda's Home", "Panda's home"));

        staticSprites.add(new StaticSprite(
                gp,
                (tileSize * 10) - 340, (tileSize * 10) - 500,
                "/tiles/Buildings/Shop1.png",
                2f, true, 10.0, 3.0, 0.0, 50.0, "Welcome to my shop!"
        ));

        // Trees
        staticSprites.add(new StaticSprite(
                gp,
                (tileSize * 40) - 100, (tileSize * 1) + 300,
                "/tiles/Buildings/Tree.png",
                2.5f, true, 3, 3, 0.0, 55.5, "A very tall tree."
        ));
        staticSprites.add(new StaticSprite(
                gp,
                (tileSize * 40) - 500, (tileSize * 1) + 200,
                "/tiles/Buildings/Tree.png",
                2.5f, true, 3, 3, 0.0, 55.5, "Another tree."
        ));

        staticSprites.add(new StaticSprite(
                gp,
                (tileSize * 40) - 900, (tileSize * 10) + 80,
                "/tiles/Buildings/Tree.png",
                2.5f, true, 3, 3, 0.0, 55.5, "A tree far away."
        ));

        staticSprites.add(new StaticSprite(
                gp,
                (tileSize * 40) + 200, (tileSize * 10),
                "/tiles/Buildings/Tree.png",
                2.5f, true, 3, 3, 0.0, 55.5, "A tree close by."
        ));

        // PALM TREES
        staticSprites.add(new StaticSprite(
                gp,
                (tileSize * 40) - 10, (tileSize * 40) + 395,
                "/tiles/Buildings/PalmTree.png",
                2.5f, true, 2.4, 2.5, 0.0, 55.5, "A palm tree."
        ));

        staticSprites.add(new StaticSprite(
                gp,
                (tileSize * 30) + 200, (tileSize * 40) + 395,
                "/tiles/Buildings/PalmTree.png",
                2.5f, true, 2.4, 2.5, 0.0, 55.5, "Another palm tree."
        ));

        // NPCs

        entity.add(new EntitySprite(
                gp, "Rizzly",
                (tileSize * 20) - 300, (tileSize * 20) - 440,
                "/entity/npc/Rizzly1.png", "/entity/npc/Rizzly2.png",
                1f, true, 35, 15, 0.0, 0, "RIZZLY: *Yawn* So tired..."
        ));

        Panda panda = new Panda(gp);
        entity.add(panda);

        entity.add(new EntitySprite(
                gp, "cop0",
                (tileSize * 20) + 325, (tileSize * 20),
                "/entity/npc/enemy/cop/copFwalk1.png", "/entity/npc/enemy/cop/copFwalk2.png",
                "/entity/npc/enemy/cop/copBwalk1.png", "/entity/npc/enemy/cop/copBwalk2.png",
                0.8999f, true, "vertical", 500, 500
        ));

        entity.add(new EntitySprite(
                gp, "cop1",
                (tileSize * 40) + 325, (tileSize * 1),
                "/entity/npc/enemy/cop/copRwalk1.png", "/entity/npc/enemy/cop/copRwalk2.png",
                "/entity/npc/enemy/cop/copLwalk1.png", "/entity/npc/enemy/cop/copLwalk2.png",
                0.8999f, true, "horizontal", 500, 500
        ));
    }
}