import com.sun.istack.internal.Nullable;

import java.awt.*;

/**
 * Created by kubut on 27.02.2016
 */
public class TranslationState {
    private Color color;
    private @Nullable String text;

    public TranslationState(Color color, @Nullable String text) {
        this.color = color;
        this.text = text;
    }

    public Color getColor() {
        return this.color;
    }

    @Nullable
    public String getText() {
        return this.text;
    }
}
