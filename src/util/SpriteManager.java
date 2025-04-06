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

        entity.add(new EntitySprite(
                gp, "Your Home",
                (tileSize * 10) + 150, (tileSize * 10) + 500,
                "/tiles/Buildings/House1.png", "/tiles/Buildings/House1.png",
                4f, true, 12, 3, 0.0, 75, "Would you like to enter your home?"

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
                2.5f, true, 3, 3, 0.0, 55.5, "An old tree."
        ));

        staticSprites.add(new StaticSprite(
                gp,
                (tileSize * 40) - 900, (tileSize * 10) + 80,
                "/tiles/Buildings/Tree.png",
                2.5f, true, 3, 3, 0.0, 55.5, "A strong tree."
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
                2.5f, true, 2.4, 2.5, 0.0, 55.5, "A chunky palm tree."
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

        //ENEMIES
        entity.add(new EntitySprite(
                gp, "cop0",
                (tileSize * 20) + 325, (tileSize * 20),
                "/entity/npc/enemy/cop/copFwalk1.png", "/entity/npc/enemy/cop/copFwalk2.png",
                "/entity/npc/enemy/cop/copBwalk1.png", "/entity/npc/enemy/cop/copBwalk2.png",
                0.8999f, true, "vertical", 500, 500, true
        ));

        entity.add(new EntitySprite(
                gp, "cop1",
                (tileSize*30)-170, (tileSize*4),
                "/entity/npc/enemy/cop/copRwalk1.png", "/entity/npc/enemy/cop/copRwalk2.png",
                "/entity/npc/enemy/cop/copLwalk1.png", "/entity/npc/enemy/cop/copLwalk2.png",
                0.8999f, true, "horizontal", 550, 500, true
        ));

        entity.add(new EntitySprite(
                gp, "cop2",
                (tileSize*30)-130, (tileSize*20)-150,
                "/entity/npc/enemy/cop/copRwalk1.png", "/entity/npc/enemy/cop/copRwalk2.png",
                "/entity/npc/enemy/cop/copLwalk1.png", "/entity/npc/enemy/cop/copLwalk2.png",
                0.8999f, true, "horizontal", 580, 500, true
        ));

        entity.add(new EntitySprite(
                gp, "cop0",
                (tileSize*40), (tileSize*20)-150,
                "/entity/npc/enemy/cop/copFwalk1.png", "/entity/npc/enemy/cop/copFwalk2.png",
                "/entity/npc/enemy/cop/copBwalk1.png", "/entity/npc/enemy/cop/copBwalk2.png",
                0.8999f, true, "vertical", 500, 500, true
        ));


        //KITTENS

        float catScale = 0.75f;
        entity.add(new EntitySprite(
                gp, "Cat00",
                (tileSize*30)-170, (tileSize*10) - 50 ,
                "/entity/kittens/batcat1.png", "/entity/kittens/batcat2.png",
                catScale, true, 35, 15, 0.0, 0, "meow"
        ));
        entity.add(new EntitySprite(
                gp, "Cat01",
                (tileSize * 20)  , (tileSize * 20) +40,
                "/entity/kittens/blackcat1.png", "/entity/kittens/blackcat2.png",
                catScale, true, 35, 15, 0.0, 0, "meow"
        ));
        entity.add(new EntitySprite(
                gp, "Cat02",
                (tileSize * 40) + 210, (tileSize * 40) + 150 ,
                "/entity/kittens/Browniecat1.png", "/entity/kittens/Browniecat1.png",
                catScale, true, 35, 15, 0.0, 0, "meow"
        ));
        entity.add(new EntitySprite(
                gp, "Cat03",
                (tileSize * 20)+10, (tileSize * 40) + 280,
                "/entity/kittens/cakecat1.png", "/entity/kittens/cakecat2.png",
                catScale, true, 35, 15, 0.0, 0, "meow"
        ));
        entity.add(new EntitySprite(
                gp, "Cat04",
                (tileSize * 20)-50, (tileSize * 40)-80,
                "/entity/kittens/calicocat1.png", "/entity/kittens/calicocat2.png",
                catScale, true, 35, 15, 0.0, 0, "meow"
        ));
        entity.add(new EntitySprite(
                gp, "Cat05",
                (tileSize * 5), (tileSize * 40)+230,
                "/entity/kittens/RoundCat1.png", "/entity/kittens/RoundCat2.png",
                catScale, true, 35, 15, 0.0, 0, "meow"
        ));
        entity.add(new EntitySprite(
                gp, "Cat06",
                (tileSize * 40)+60, (tileSize * 1) + 370,
                "/entity/kittens/SiameseCat1.png", "/entity/kittens/SiameseCat2.png",
                catScale, true, 35, 15, 0.0, 0, "meow"
        ));
        entity.add(new EntitySprite(
                gp, "Cat07",
                (tileSize * 30) + 100, (tileSize * 40) + 300 ,
                "/entity/kittens/spotcat1.png", "/entity/kittens/spotcat2.png",
                catScale, true, 35, 15, 0.0, 0, "meow"
        ));

        entity.add(new EntitySprite(
                gp, "Cat08",
                (tileSize * 30) + 100, (tileSize * 40) + 300 ,
                "/entity/kittens/spotcat1.png", "/entity/kittens/spotcat2.png",
                catScale, true, 35, 15, 0.0, 0, "meow"
        ));

        entity.add(new EntitySprite(
                gp, "Cat06",
                (tileSize*34), (tileSize*20),
                "/entity/kittens/SiameseCat1.png", "/entity/kittens/SiameseCat2.png",
                catScale, true, 35, 15, 0.0, 0, "meow"
        ));
    }
}