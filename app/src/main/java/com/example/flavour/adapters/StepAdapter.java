package com.example.flavour.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.flavour.R;
import com.example.flavour.databinding.RecipeBinding;
import com.example.flavour.databinding.StepBinding;
import com.example.flavour.models.Recipe;
import com.example.flavour.models.Step;

import java.util.Objects;

public class StepAdapter extends ListAdapter<Step, StepAdapter.StepHolder> {

    private final Recipe recipe;
    private StepEvents events;

    public StepAdapter(Recipe recipe) {
        super(new StepDiffCallback());
        this.recipe = recipe;
    }

    public static class StepHolder extends RecyclerView.ViewHolder {

        RecipeBinding recipe;
        StepBinding step;

        public StepHolder(RecipeBinding recipe) {
            super(recipe.getRoot());
            this.recipe = recipe;
        }

        public StepHolder(StepBinding step) {
            super(step.getRoot());
            this.step = step;
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) return 0;
        return 1;
    }

    @NonNull
    @Override
    public StepHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) return new StepHolder(
                RecipeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)
        );

        return new StepHolder(
                StepBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)
        );
    }
    public interface StepEvents {
        void toFavorite(boolean is_favorite);
        void saveFile();
    }
    @Override
    public void onBindViewHolder(@NonNull StepHolder holder, int position) {
        if (getItemViewType(position) == 0) {
            holder.recipe.add.setText(recipe.isFavorite() ? "Удалить из избранных" : "Добавить в избранные");
            setRatingImages(recipe.getDifficulty(), holder.recipe.container);
            Glide
                    .with(holder.itemView)
                    .load(recipe.getImage())
                    .into(holder.recipe.imageRecipe);
            String dur = recipe.getDuration() + " минут";
            holder.recipe.duration.setText(dur);
            holder.recipe.ingr.setText(setIngredients(recipe));
            holder.recipe.add.setOnClickListener(v -> {
                if (events == null) return;
                events.toFavorite(recipe.isFavorite());
                recipe.setFavorite(!recipe.isFavorite());
                holder.recipe.add.setText(recipe.isFavorite() ? "Удалить из избранных" : "Добавить в избранные");
            });
            holder.recipe.loadFile.setOnClickListener(v -> {
                if (events == null) return;
                events.saveFile();
            });
            return;
        }

        Step step = getItem(position - 1);
        holder.step.numberStep.setText(String.valueOf(step.getStep()));
        holder.step.textStep.setText(step.getDescription());
        Glide
                .with(holder.itemView)
                .load(step.getImage())
                .into(holder.step.image);
    }

    private String setIngredients(Recipe recipe) {
        String ingredients = "";
        for (int i = 0; i < recipe.getIngredients().size(); i++) {
            ingredients += "• " + recipe.getIngredients().get(i) + "\n";
        }
        return ingredients;
    }

    private void setRatingImages(int rating, LinearLayout layout) {
        for (int i = 0; i < 5; i++) {
            ImageView rView = new ImageView(layout.getContext());
            rView.setImageResource(R.drawable.chick_bw);
            rView.setLayoutParams(new ViewGroup.LayoutParams(150, 150));
            if (i < rating) {
                rView.setImageResource(R.drawable.chick);
            }
            layout.addView(rView);
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + 1;
    }

    private static class StepDiffCallback extends DiffUtil.ItemCallback<Step> {
        @Override
        public boolean areItemsTheSame(@NonNull Step oldItem, @NonNull Step newItem) {
            return Objects.equals(oldItem.getStep(), newItem.getStep());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Step oldItem, @NonNull Step newItem) {
            return oldItem.getStep() == newItem.getStep();
        }
    }

    public void setEvents(StepEvents events) {
        this.events = events;
    }
}
