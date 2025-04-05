package entity.npc;

import Main.GamePanel;
import util.EntitySprite;

public class Panda extends EntitySprite {

    public Panda(GamePanel gp){
        super(gp, "Panda",
                (gp.getTileSize() * 10) - 450, (gp.getTileSize() * 10) - 400,
                "/entity/npc/Panda1.png", "/entity/npc/Panda2.png",
                1f, true, 35, 45, 0.0, 0, "PANDA: Can I get you anything?");
    }
}
