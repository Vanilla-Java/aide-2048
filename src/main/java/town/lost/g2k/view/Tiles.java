package town.lost.g2k.view;

import java.awt.*;

public final class Tiles {
    private Tiles() {
    }

    static Color getTileColor(int value) {
        switch (value) {
            case 2:
                return new Color(0xeee4da);
            case 4:
                return new Color(0xede0c8);
            case 8:
                return new Color(0xf2b179);
            case 16:
                return new Color(0xf59563);
            case 32:
                return new Color(0xf67c5f);
            case 64:
                return new Color(0xf65e3b);
            case 128:
                return new Color(0xE0EDC8);
            case 256:
                return new Color(0xB1F279);
            case 512:
                return new Color(0x95F563);
            case 1024:
                return new Color(0x7CF65F);
            case 2048:
                return new Color(0x5EF63B);
            case 4096:
                return new Color(0x2ec2ed);
            case 8192:
                return new Color(0x79b1f2);
            case 16384:
                return new Color(0x6395f5);
            case 32768:
                return new Color(0x5f7cf6);
            case 65536:
                return new Color(0x3b5ef6);
            default:
                // For tiles > 2048 or unknown
                return new Color(0xfcea12);
        }
    }
}
