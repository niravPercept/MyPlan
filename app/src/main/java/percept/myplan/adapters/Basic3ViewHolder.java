package percept.myplan.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by percept on 5/8/16.
 */

public abstract class Basic3ViewHolder extends RecyclerView.ViewHolder {

    static int TYPE_VIDEO = 1;

    static int TYPE_NORMAL = 2;

    static int TYPE_IMAGE = 3;

    static int TYPE_AUDIO = 4;

    public Basic3ViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void bind(RecyclerView.Adapter adapter, Object item,int position);

    /**
     * Setup click listener to current ViewHolder. {@link RecyclerView.ViewHolder#itemView} will
     * catch the event by default. Override this to setup proper Views to receive the click event.
     */
    public void setOnItemClickListener(View.OnClickListener listener) {
        this.itemView.setOnClickListener(listener);
    }

    /**
     * Setup long click listener to current ViewHolder. {@link RecyclerView.ViewHolder#itemView} will
     * catch the event by default. Override this to setup proper Views to receive the click event.
     */
    public void setOnItemLongClickListener(View.OnLongClickListener listener) {
        this.itemView.setOnLongClickListener(listener);
    }

}
