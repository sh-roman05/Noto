package com.roman.noto.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NoteColor
{
    public class ItemColor
    {
        private String colorPrimaryDark;
        private String colorPrimary;
        private String colorBackground;
        private int index;

        public int getIndex() {
            return index;
        }

        public String getColorPrimaryDark() {
            return colorPrimaryDark;
        }

        public String getColorPrimary() {
            return colorPrimary;
        }

        public String getColorBackground() {
            return colorBackground;
        }

        public ItemColor(String colorPrimaryDark, String colorPrimary, String colorBackground, int index)
        {
            this.colorPrimaryDark = colorPrimaryDark;
            this.colorPrimary = colorPrimary;
            this.colorBackground = colorBackground;
            this.index = index;
        }
    }

    private static NoteColor INSTANCE = null;

    public static NoteColor getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NoteColor();
        }
        return INSTANCE;
    }

    public final ItemColor[] colors =
            {
                    new ItemColor("#BDBDBD", "#E0E0E0", "#EEEEEE", 0), //Gray
                    new ItemColor("#EF5350", "#E57373", "#EF9A9A", 1), //Red
                    new ItemColor("#EC407A", "#F06292", "#F48FB1", 2), //Pink
                    new ItemColor("#AB47BC", "#BA68C8", "#CE93D8", 3), //Purple
                    new ItemColor("#7E57C2", "#9575CD", "#B39DDB", 4), //Deep purple
                    new ItemColor("#5C6BC0", "#7986CB", "#9FA8DA", 5), //Indigo
                    new ItemColor("#42A5F5", "#64B5F6", "#90CAF9", 6), //Blue
                    new ItemColor("#29B6F6", "#4FC3F7", "#81D4FA", 7), //Light Blue
                    new ItemColor("#26C6DA", "#4DD0E1", "#80DEEA", 8), //Cyan
                    new ItemColor("#26A69A", "#4DB6AC", "#80CBC4", 9), //Teal
                    new ItemColor("#66BB6A", "#81C784", "#A5D6A7", 10), //Green
                    new ItemColor("#9CCC65", "#AED581", "#C5E1A5", 11), //Light Green
                    new ItemColor("#D4E157", "#DCE775", "#E6EE9C", 12), //Lime
                    new ItemColor("#FFEE58", "#FFF176", "#FFF59D", 13), //Yellow
                    new ItemColor("#FFCA28", "#FFD54F", "#FFE082", 14), //Amber
                    new ItemColor("#FFA726", "#FFB74D", "#FFCC80", 15), //Orange
                    new ItemColor("#FF7043", "#FF8A65", "#FFAB91", 16), //Deep Orange
                    new ItemColor("#8D6E63", "#A1887F", "#BCAAA4", 17), //Brown
                    new ItemColor("#78909C", "#90A4AE", "#B0BEC5", 18) //Blue Gray
            };

    public List<ItemColor> getList()
    {
        return new ArrayList<>(Arrays.asList(colors));
    }

    public ItemColor getItemColor(int index)
    {
        if(colors.length - 1 < index)
        {
            return colors[0];
        }
        else return colors[index];

    }


}
