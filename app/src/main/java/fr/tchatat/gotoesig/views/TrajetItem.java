package fr.tchatat.gotoesig.views;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xwray.groupie.GroupieViewHolder;
import com.xwray.groupie.Item;
import com.xwray.groupie.OnItemClickListener;
import com.xwray.groupie.OnItemLongClickListener;

public class TrajetItem extends GroupieViewHolder {
    public TrajetItem(@NonNull View rootView) {
        super(rootView);
    }

    @Override
    public void bind(@NonNull Item item, @Nullable OnItemClickListener onItemClickListener, @Nullable OnItemLongClickListener onItemLongClickListener) {
        super.bind(item, onItemClickListener, onItemLongClickListener);
    }

    @Override
    public void unbind() {
        super.unbind();
    }

    /*
    @Override
    public void bind(@NonNull Item item, @Nullable OnItemClickListener onItemClickListener, @Nullable OnItemLongClickListener onItemLongClickListener) {
        super.bind(item, onItemClickListener, onItemLongClickListener);

    }*/



    /* override fun bind(viewHolder:GroupieViewHolder, position: Int) {
        viewHolder.itemView.new_message_list_username.text = user.username

        Picasso.get().load(user.profileImage).into(viewHolder.itemView.pp_new_message)
    }

    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }*/
}
