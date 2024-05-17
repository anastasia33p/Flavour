package com.example.flavour.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.flavour.databinding.CardBinding;
import com.example.flavour.models.Recipe;
import com.example.flavour.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RecipeAdapter extends ListAdapter<Recipe, RecipeAdapter.RecipeHolder> {
    private RecipeEvents events;
    public List<Recipe> recipes;

    public RecipeAdapter() {
        super(new RecipeDiffCallback());
    }

    public void setEvents(RecipeEvents events) {
        this.events = events;
    }


    @NonNull
    @Override
    public RecipeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardBinding binding = CardBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new RecipeHolder(binding);
    }


    public interface RecipeEvents {
        void openRecipe(Recipe recipe);

        void toFavorite(Recipe recipe, boolean is_favorite);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeHolder holder, int position) {
        Recipe recipe = getItem(position);
        holder.binding.nameRecipe.setText(recipe.getName());
        holder.binding.descriptionRecipe.setText("Сложность: " + recipe.getDifficulty() + " из 5 \nВремя приготовления: " + recipe.getDuration() + " минут");
        holder.binding.favoriteButton.setChecked(recipe.isFavorite());
        Glide
                .with(holder.itemView)
                .load(recipe.getImage())
                .into(holder.binding.imageRecipe);

        holder.binding.favoriteButton.setChecked(recipe.isFavorite());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get().addOnCompleteListener(snapshotTask -> {
                    User current = snapshotTask.getResult().getValue(User.class);
                    if (current != null && current.getFavorites() != null
                            && current.getFavorites().contains(recipe.getId())) {
                        holder.binding.favoriteButton.setChecked(true);
                        recipe.setFavorite(true);
                    }
                });

        holder.binding.favoriteButton.setOnClickListener(v -> {
            if (events == null) return;
            events.toFavorite(recipe, recipe.isFavorite());
            recipe.setFavorite(!recipe.isFavorite());
            holder.binding.favoriteButton.setChecked(recipe.isFavorite());
        });

        holder.binding.background.setOnClickListener(v -> {
            if (events == null) return;
            events.openRecipe(recipe);
        });
    }

    public static class RecipeHolder extends RecyclerView.ViewHolder {
        CardBinding binding;

        public RecipeHolder(@NonNull CardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private static class RecipeDiffCallback extends DiffUtil.ItemCallback<Recipe> {
        @Override
        public boolean areItemsTheSame(@NonNull Recipe oldItem, @NonNull Recipe newItem) {
            return Objects.equals(oldItem.getId(), newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Recipe oldItem, @NonNull Recipe newItem) {
            return oldItem.getId().equals(newItem.getId());
        }
    }
    public void search(CharSequence query) {
        if (query==""){
            submitList(recipes);
        }
        if (recipes == null) return;
        List<Recipe> filteredList = recipes.stream()
                .filter(recipe -> recipe.getName().toLowerCase().contains(query.toString().toLowerCase()))
                .collect(Collectors.toList());
        submitList(filteredList);
    }

    public void submitList(List<Recipe> recipes) {
        if (this.recipes == null){
            this.recipes = recipes;
        }
        super.submitList(recipes);
    }

}
