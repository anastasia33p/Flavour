package com.example.flavour.adapters;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
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
                if (events != null) {
                    events.SaveData(recipe, new ArrayList<>(getCurrentList()));
                }
            });
            holder.last.add.setOnClickListener(v -> {
               if (events != null) {
                   events.addStep();}
            });
        } else {
            Step step = getCurrentList().get(position - 1);
            holder.step.stepNum.setText("Шаг" + String.valueOf(position));
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
                recipe.setDifficulty((Integer.parseInt(s.toString())));
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
}
