package net.volcaronitee.taraton.util.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

/**
 * Represents the content of a line in an overlay, which can consist of multiple columns.
 */
public class LineContent {
    private static final int FONT_SIZE = 9;
    private static final int ITEM_SIZE = 16;

    private static final int MARGIN = 4;

    private List<List<Object>> content = new ArrayList<>();
    private Supplier<Boolean> shouldRender;
    private boolean changed = true;

    private List<Integer> columnWidths = new ArrayList<>();
    private int width = 0;
    private int height = 0;

    /**
     * Creates a new LineContent instance with the specified content and rendering condition.
     * 
     * @param content The content of the line, represented as a list of columns.
     * @param shouldRender A supplier that determines if the line should be rendered.
     */
    private LineContent(List<List<Object>> content, Supplier<Boolean> shouldRender) {
        this.content.addAll(content);
        this.shouldRender = shouldRender;
    }

    /**
     * Creates a LineContent instance from a list of columns, where each column can contain multiple
     * items or a single item.
     * 
     * @param content The content of the line, represented as a list of objects, where each object
     *        can be a List or a single item.
     * @param shouldRender Supplier that determines if the line should be rendered.
     * @return A new LineContent instance containing the specified columns.
     */
    public static LineContent ofColumns(List<Object> content, Supplier<Boolean> shouldRender) {
        List<List<Object>> columns = new ArrayList<>();

        // Iterate through the content and create columns
        for (Object item : content) {
            if (item instanceof List<?> list) {
                // If the item is a list, add it as a new column
                columns.add(new ArrayList<>(list));
            } else {
                // Otherwise, add the item as a single column
                columns.add(new ArrayList<>(List.of(item)));
            }
        }

        return new LineContent(columns, shouldRender);
    }

    /**
     * Creates a LineContent instance from a list of objects, where each object can be a Text,
     * ItemStack, or String.
     * 
     * @param content The content of the line, represented as a list of objects, where each object
     *        can be a Text, ItemStack, or String.
     * @param shouldRender Supplier that determines if the line should be rendered.
     * @return A new LineContent instance containing the specified content.
     */
    public static LineContent of(List<Object> content, Supplier<Boolean> shouldRender) {
        return new LineContent(new ArrayList<>(List.of(new ArrayList<>(content))), shouldRender);
    }

    /**
     * Creates a LineContent instance from a Text object.
     * 
     * @param text The Text object to be included in the line content.
     * @param shouldRender Supplier that determines if the line should be rendered.
     * @return A new LineContent instance containing the specified Text.
     */
    public static LineContent of(Text text, Supplier<Boolean> shouldRender) {
        return of(new ArrayList<>(List.of(text)), shouldRender);
    }

    /**
     * Creates a LineContent instance from an ItemStack.
     * 
     * @param stack The ItemStack to be included in the line content.
     * @param shouldRender Supplier that determines if the line should be rendered.
     * @return A new LineContent instance containing the specified ItemStack.
     */
    public static LineContent of(ItemStack stack, Supplier<Boolean> shouldRender) {
        return of(new ArrayList<>(List.of(stack)), shouldRender);
    }

    /**
     * Creates a LineContent instance from a String.
     * 
     * @param text The String to be included in the line content.
     * @param shouldRender Supplier that determines if the line should be rendered.
     * @return A new LineContent instance containing the specified String.
     */
    public static LineContent of(String text, Supplier<Boolean> shouldRender) {
        return of(new ArrayList<>(List.of(text)), shouldRender);
    }

    /**
     * Creates a deep copy of the LineContent instance.
     * 
     * @param other The LineContent instance to copy.
     */
    public LineContent(LineContent other) {
        // Deep copy the content to ensure new pointers
        for (List<Object> column : other.content) {
            List<Object> newColumn = new ArrayList<>();
            for (Object item : column) {
                newColumn.add(item);
            }
            this.content.add(newColumn);
        }

        this.shouldRender = other.shouldRender;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getColumns() {
        return content.size();
    }

    public int getColumnWidth(int columnIndex) {
        if (columnIndex < 0 || columnIndex >= columnWidths.size()) {
            return 0;
        }
        return columnWidths.get(columnIndex);
    }

    public boolean isChanged() {
        return changed;
    }

    public boolean shouldRender() {
        return shouldRender.get();
    }

    /**
     * Gets the content of a specific column in the line.
     * 
     * @param columnIndex The index of the column to retrieve.
     */
    public List<Object> getColumn(int columnIndex) {
        if (columnIndex < 0 || columnIndex >= content.size()) {
            return null;
        }

        content.get(columnIndex);
        return content.get(columnIndex);
    }

    /**
     * Sets the content of a specific column in the line.
     * 
     * @param item The item or list of items to set in the column.
     * @param columnIndex The index of the column to set the content for.
     */
    public void setColumn(Object item, int columnIndex) {
        if (columnIndex < 0 || columnIndex >= content.size()) {
            return;
        }

        List<Object> column = content.get(columnIndex);
        column.clear();

        if (item instanceof List<?> list) {
            // If the item is a list, add all items to the column
            column.addAll(list);
        } else {
            // Otherwise, add the single item to the column
            column.add(item);
        }

        changed = true;
    }

    /**
     * Calculates the size of the line content based on its items and columns.
     */
    public void calculateSize() {
        columnWidths.clear();
        width = MARGIN * (content.size() - 1);
        height = 0;
        TextRenderer tr = MinecraftClient.getInstance().textRenderer;

        for (List<Object> column : content) {
            for (Object item : column) {
                int columnWidth = 0;
                if (item instanceof ItemStack stack && !stack.isEmpty()) {
                    // If the item is an ItemStack, use its size for height and width
                    height = Math.max(height, ITEM_SIZE);
                    columnWidth = ITEM_SIZE;
                } else if (item instanceof String text) {
                    // If the item is a String, use the text renderer to get its width
                    height = Math.max(height, FONT_SIZE);
                    columnWidth = tr.getWidth(text);
                } else if (item instanceof Text text) {
                    // If the item is a Text object, use the text renderer to get its width
                    height = Math.max(height, FONT_SIZE);
                    columnWidth = tr.getWidth(text);
                }

                width += columnWidth;
                columnWidths.add(columnWidth);
            }
        }

        changed = false;
    }

    /**
     * Draws the line content at the specified position with the given scale and alignment.
     * 
     * @param context The context to use for drawing the content.
     * @param x The X position to start drawing the content.
     * @param y The Y position to start drawing the content.
     * @param scale The scale factor to apply to the content size.
     * @param align The alignment of the content (0: left, 1: center, 2: right).
     * @param maxColumnWidths The maximum widths of each column, used for alignment calculations.
     */
    public void draw(DrawContext context, int x, int y, float scale, int align,
            List<Integer> maxColumnWidths) {
        TextRenderer tr = MinecraftClient.getInstance().textRenderer;
        float currentX = x;

        for (int i = 0; i < this.content.size(); i++) {
            List<Object> column = this.content.get(i);
            float columnWidth = this.columnWidths.get(i);
            float maxColumnWidth = maxColumnWidths.get(i);

            // Calculate the starting X position for the column based on alignment
            float columnStartX = currentX;
            if (align == 1) { // Center
                columnStartX += (maxColumnWidth - columnWidth) * scale / 2;
            } else if (align == 2) { // Right
                columnStartX += (maxColumnWidth - columnWidth) * scale;
            }

            // Draw each item in the column
            float offsetX = 0;
            for (Object item : column) {
                float itemHeight = (item instanceof ItemStack ? ITEM_SIZE : FONT_SIZE);
                float drawX = columnStartX + offsetX;
                float drawY = y + (this.height * scale - itemHeight) / 2;

                // Push the current matrix state and apply transformations
                context.getMatrices().push();
                context.getMatrices().translate(drawX, drawY, 0);
                context.getMatrices().scale(scale, scale, 1.0f);

                if (item instanceof ItemStack stack && !stack.isEmpty()) {
                    // If the item is an ItemStack, draw it
                    context.drawItem(stack, 0, 0);
                    offsetX += ITEM_SIZE * scale;
                } else if (item instanceof String text) {
                    // If the item is a String, draw it as text
                    context.drawTextWithShadow(tr, text, 0, 0, Colors.WHITE);
                    offsetX += tr.getWidth(text) * scale;
                } else if (item instanceof Text text) {
                    // If the item is a Text object, draw it as text
                    context.drawTextWithShadow(tr, text, 0, 0, Colors.WHITE);
                    offsetX += tr.getWidth(text) * scale;
                }

                context.getMatrices().pop();
            }
            currentX += (maxColumnWidth + MARGIN) * scale;
        }
    }
}
