package com.example.flavour.adapters;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.flavour.databinding.CardBinding;
import com.example.flavour.databinding.EditorFirstBinding;
import com.example.flavour.databinding.EditorLastBinding;
import com.example.flavour.databinding.StepEditBinding;
import com.example.flavour.models.Recipe;
import com.example.flavour.models.Step;
import com.example.flavour.views.MainActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ItemAdapter extends ListAdapter<Step, ItemAdapter.ItemHolder> {
    private final Recipe recipe;
    private ItemEvents events;

    public ItemAdapter(Recipe recipe) {
        super(new ItemDiffCallback());
        this.recipe = recipe;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return 0;
        else if (position == getItemCount() - 1) return 2;
        return 1;
    }

    public interface ItemEvents {
        void showTimePickerDialog();

        void SaveData(Recipe recipe, ArrayList<Step> list);

        void chooseImage(int position);

        void addStep();
    }

    public void setEvents(ItemEvents events) {
        this.events = events;
    }

    @NonNull
    @Override
    public ItemAdapter.ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return new ItemHolder(
                    EditorFirstBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)
            );
        } else if (viewType == 1) return new ItemHolder(
                StepEditBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)
        );
        else return new ItemHolder(
                    EditorLastBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)
            );
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.ItemHolder holder, int position) {
        if (position == 0) {
            setListeners(holder);
            holder.first.nameInp.setText(recipe.getName());
            if(recipe.getDifficulty()!=0){
                holder.first.diffInp.setText(String.valueOf(recipe.getDifficulty()));
            }
            holder.first.ingridientsInp.setText(recipe.getIngredients().stream().collect(Collectors.joining(";")));
            if (recipe.getDuration() != 0) {
                holder.first.editTextTime.setText(String.valueOf(recipe.getDuration()));
            }
            holder.first.load.setOnClickListener(v -> {
                if (events != null) {
                    events.chooseImage(position);
                }
            });
            if (recipe.getImage() != null) {
                Glide.with(holder.first.photoRecipe.getContext())
                        .load(recipe.getImage())
                        .apply(RequestOptions.centerCropTransform())
                        .addListener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e,
                                                        @Nullable Object model,
                                                        @NonNull Target<Drawable> target,
                                                        boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(@NonNull Drawable resource,
                                                           @NonNull Object model,
                                                           Target<Drawable> target,
                                                           @NonNull DataSource dataSource,
                                                           boolean isFirstResource) {
                                holder.first.photoRecipe.setVisibility(View.VISIBLE);
                                return false;
                            }
                        })
                        .into(holder.first.photoRecipe);
            }
        } else if (position == getItemCount() - 1) {
            holder.last.save.setOnClickListener(v -> {
                if (events != null && checkInputs(holder.last.save.getContext())) {
                    events.SaveData(recipe, new ArrayList<>(getCurrentList()));
                }
            });
            holder.last.add.setOnClickListener(v -> {
               if (events != null) {
                   events.addStep();}
            });
        } else {
            Step step = getCurrentList().get(position - 1);
            holder.step.stepNum.setText("Шаг " + String.valueOf(position));
            holder.step.nameInp.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    step.setDescription(s.toString());
                }
            });
            if (step.getImage() != null) {
                Glide.with(holder.step.photoStep.getContext())
                        .load(step.getImage())
                        .apply(RequestOptions.centerCropTransform())
                        .addListener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e,
                                                        @Nullable Object model,
                                                        @NonNull Target<Drawable> target,
                                                        boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(@NonNull Drawable resource,
                                                           @NonNull Object model,
                                                           Target<Drawable> target,
                                                           @NonNull DataSource dataSource,
                                                           boolean isFirstResource) {
                                holder.step.photoStep.setVisibility(View.VISIBLE);
                                return false;
                            }
                        })
                        .into(holder.step.photoStep);
            }
            holder.step.load.setOnClickListener(v -> {
                if (events != null) {
                    events.chooseImage(position);
                }
            });
            holder.step.nameInp.setText(step.getDescription());
        }
    }

    public void setListeners(ItemHolder holder) {
        holder.first.nameInp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                recipe.setName(s.toString());
            }
        });
        holder.first.diffInp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    recipe.setDifficulty((Integer.parseInt(s.toString())));
                }

            }
        });
        holder.first.ingridientsInp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String ings = s.toString();
                ings.replaceAll("\n", " ");
                while (ings.contains("  ")) {
                    ings.replaceAll("  ", " ");
                }

                recipe.setIngredients((new ArrayList<>(Arrays.asList(ings.split(";")))));
            }
        });
        holder.first.editTextTime.setOnClickListener(v -> {
            if (events == null) return;
            events.showTimePickerDialog();
        });
    }

    public void updateTime(int hour, int minute) {
        recipe.setDuration(hour * 60 + minute);
        notifyItemChanged(0);
    }

    public void setImage(String image, int position) {
        if (position == 0) {
            recipe.setImage(image);
        } else {
            getCurrentList().get(position - 1).setImage(image);
        }
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + 2;
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {
        StepEditBinding step;
        EditorFirstBinding first;
        EditorLastBinding last;

        public ItemHolder(@NonNull StepEditBinding binding) {
            super(binding.getRoot());
            this.step = binding;
        }

        public ItemHolder(@NonNull EditorFirstBinding binding) {
            super(binding.getRoot());
            this.first = binding;
        }

        public ItemHolder(@NonNull EditorLastBinding binding) {
            super(binding.getRoot());
            this.last = binding;
        }
    }

    private static class ItemDiffCallback extends DiffUtil.ItemCallback<Step> {
        @Override
        public boolean areItemsTheSame(@NonNull Step oldItem, @NonNull Step newItem) {
            return Objects.equals(oldItem.getStep(), newItem.getStep());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Step oldItem, @NonNull Step newItem) {
            return oldItem.getStep() == (newItem.getStep());
        }
    }

    private boolean checkInputs(Context context) {
        if (recipe.getImage() == null) {
            Toast.makeText(context,"Добавьте изображение рецепта",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (recipe.getName().isEmpty()) {
            Toast.makeText(context,"Введите название рецепта",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (recipe.getName().length()>15) {
            Toast.makeText(context,"Слишком длинное название рецепта",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (recipe.getDifficulty() == 0) {
            Toast.makeText(context,"Выберите сложность рецепта",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (recipe.getDuration() == 0) {
            Toast.makeText(context,"Выберите время приготовления рецепта",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (recipe.getIngredients().size() == 1 && recipe.getIngredients().get(0)=="") {
            Toast.makeText(context,"Добавьте ингредиенты рецепта",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (recipe.getDifficulty()<0 || recipe.getDifficulty()>5) {
            Toast.makeText(context,"Некорректное значение сложности рецепта",Toast.LENGTH_SHORT).show();
            return false;
        }
        for (Step step : getCurrentList()) {
            if (step==null) {
                Toast.makeText(context,"Добавьте шаги рецепта",Toast.LENGTH_SHORT).show();
                return false;
            }
            if (step.getImage() == null || step.getImage().isEmpty()) {
                Toast.makeText(context,"Добавьте изображения к шагу рецепта",Toast.LENGTH_SHORT).show();
                return false;
            }
            if (step.getDescription().isEmpty()) {
                Toast.makeText(context,"Добавьте описание шага рецепта",Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }
}
