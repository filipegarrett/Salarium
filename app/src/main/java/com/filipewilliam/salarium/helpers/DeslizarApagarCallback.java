package com.filipewilliam.salarium.helpers;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.filipewilliam.salarium.R;
import com.filipewilliam.salarium.adapter.ContasVencerAdapter;

public class DeslizarApagarCallback extends ItemTouchHelper.SimpleCallback {

    private ContasVencerAdapter contasVencerAdapter;
    private Drawable iconeLixeira;
    private final ColorDrawable fundoExlcuir;

    public DeslizarApagarCallback(ContasVencerAdapter adapter){
        super(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        contasVencerAdapter = adapter;
        iconeLixeira = ContextCompat.getDrawable(contasVencerAdapter.gerarContext(), R.drawable.ic_lixeira_excluir_branco_24dp);
        fundoExlcuir = new ColorDrawable(Color.RED);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        int posicao =  viewHolder.getAdapterPosition();
        contasVencerAdapter.excluirItem(posicao);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        View itemView = viewHolder.itemView;
        int backgroundCornerOffset = 20; //so background is behind the rounded corners of itemView

        int iconMargin = (itemView.getHeight() - iconeLixeira.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - iconeLixeira.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + iconeLixeira.getIntrinsicHeight();

        if (dX > 0) { // Swiping to the right
            int iconLeft = itemView.getLeft() + iconMargin;
            int iconRight = itemView.getLeft() + iconMargin + iconeLixeira.getIntrinsicWidth();
            iconeLixeira.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            fundoExlcuir.setBounds(itemView.getLeft(), itemView.getTop(),
                    itemView.getLeft() + ((int) dX) + backgroundCornerOffset, itemView.getBottom());
        } else if (dX < 0) { // Swiping to the left
            int iconLeft = itemView.getRight() - iconMargin - iconeLixeira.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            iconeLixeira.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            fundoExlcuir.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                    itemView.getTop(), itemView.getRight(), itemView.getBottom());
        } else { // view is unSwiped
            fundoExlcuir.setBounds(0, 0, 0, 0);
        }

        fundoExlcuir.draw(c);
        iconeLixeira.draw(c);
    }
}
