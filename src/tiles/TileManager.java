package tiles;

import Main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TileManager {
    GamePanel gp;
    Tile[] tile;
    public int[][] mapTileNum;
    int maxCol;
    int maxRow;

    public TileManager(GamePanel gp) {
        this.gp = gp;
        tile = new Tile[15];

        maxCol = gp.maxWorldCol;
        maxRow = gp.maxWorldRow;
        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];
        getTileImage();
        loadMap();
    }

    public void getTileImage() {
        try {
            tile[0] = new Tile();
            tile[0].image = ImageIO.read(getClass().getResourceAsStream("/tiles/outdoorsLayer01/Empty.png"));

            tile[1] = new Tile();
            tile[1].image = ImageIO.read(getClass().getResourceAsStream("/tiles/outdoorsLayer01/Water1.png"));
            tile[1].collision = true;

            tile[2] = new Tile();
            tile[2].image = ImageIO.read(getClass().getResourceAsStream("/tiles/outdoorsLayer01/Water2.png"));
            tile[2].collision = true;

            tile[3] = new Tile();
            tile[3].image = ImageIO.read(getClass().getResourceAsStream("/tiles/outdoorsLayer01/Dirty.png"));

            tile[4] = new Tile();
            tile[4].image = ImageIO.read(getClass().getResourceAsStream("/tiles/outdoorsLayer01/Empty.png"));
            tile[4].collision = true;

            tile[5] = new Tile();
            tile[5].image = ImageIO.read(getClass().getResourceAsStream("/tiles/outdoorsLayer01/Grass.png"));


            tile[6] = new Tile();
            tile[6].image = ImageIO.read(getClass().getResourceAsStream("/tiles/outdoorsLayer01/Wall1.png"));
            tile[6].collision = true;

            tile[7] = new Tile();
            tile[7].image = ImageIO.read(getClass().getResourceAsStream("/tiles/outdoorsLayer01/Wall2.png"));
            tile[7].collision = true;

            tile[8] = new Tile();
            tile[8].image = ImageIO.read(getClass().getResourceAsStream("/tiles/outdoorsLayer02/FruitBush.png"));
            tile[8].collision = true;

            tile[9] = new Tile();
            tile[9].image = ImageIO.read(getClass().getResourceAsStream("/tiles/outdoorsLayer02/BlueTree.png"));
            tile[9].collision = true;

            tile[10] = new Tile();
            tile[10].image = ImageIO.read(getClass().getResourceAsStream("/tiles/outdoorsLayer02/Bush1.png"));
            tile[10].collision = true;

            tile[11] = new Tile();
            tile[11].image = ImageIO.read(getClass().getResourceAsStream("/tiles/outdoorsLayer02/Bush2.png"));
            tile[11].collision = true;

            tile[12] = new Tile();
            tile[12].image = ImageIO.read(getClass().getResourceAsStream("/tiles/outdoorsLayer02/Fern.png"));

            tile[13] = new Tile();
            tile[13].image = ImageIO.read(getClass().getResourceAsStream("/tiles/outdoorsLayer02/Rock.png"));
            tile[13].collision = true;

            tile[14] = new Tile();
            tile[14].image = ImageIO.read(getClass().getResourceAsStream("/tiles/outdoorsLayer02/Rock2.png"));
            tile[14].collision = true;

        } catch (IOException e) {
            System.err.println("Error loading tile images:");
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2) {
        int worldCol = 0;
        int worldRow = 0;

        while (worldCol < gp.maxWorldCol && worldRow < gp.maxWorldRow) {
            int tileNum = mapTileNum[worldCol][worldRow];

            int worldX = worldCol * gp.getTileSize();
            int worldY = worldRow * gp.getTileSize();
            int screenX = worldX - gp.playerMovement.worldX + gp.playerMovement.screenX;
            int screenY = worldY - gp.playerMovement.worldY + gp.playerMovement.screenY;

            if (screenX > -gp.getTileSize() &&
                    screenX < gp.getScreenWidth() &&
                    screenY > -gp.getTileSize() &&
                    screenY < gp.getScreenHeight()) {

                if (tileNum >= 0 && tileNum < tile.length) {
                    g2.drawImage(tile[tileNum].image, screenX, screenY, gp.getTileSize(), gp.getTileSize(), null);
                }
            }

            worldCol++;

            if (worldCol == gp.maxWorldCol) {
                worldCol = 0;
                worldRow++;
            }
        }
    }

    public Tile[] getTile() {
        return tile;
    }

    public void loadMap() {
        try {
            InputStream is = getClass().getResourceAsStream("/maps/world01");
            if (is == null) {
                System.err.println("ERROR: Could not find map file: /maps/world01");
                return;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            int col = 0;
            int row = 0;
            String line;

            while (row < gp.maxWorldRow && (line = br.readLine()) != null) {
                String[] numbers = line.split(",");
                col = 0;
                while (col < gp.maxWorldCol && col < numbers.length) {
                    int num = Integer.parseInt(numbers[col]);
                    mapTileNum[col][row] = num;
                    col++;
                }
                while (col < gp.maxWorldCol) {
                    mapTileNum[col][row] = 0;
                    col++;
                }
                row++;
            }
            br.close();
        } catch (Exception e) {
            System.err.println("Error loading map:");
            e.printStackTrace();
        }
    }
}