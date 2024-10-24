package com.example.dashboard;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private LinearLayout categoryContainer;
    private CircularProgressIndicator overallProgressIndicator;
    private TextView progressPercentage;
    private List<Category> categories = new ArrayList<>();
    private RecyclerView recentActivityRecyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> recentTasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupRecyclerView();
    }

    private void initializeViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        categoryContainer = findViewById(R.id.category_container);
        overallProgressIndicator = findViewById(R.id.overall_progress);
        progressPercentage = findViewById(R.id.progress_percentage);
        updateProgress(0);

        FloatingActionButton fab = findViewById(R.id.fab_add_task);
        fab.setOnClickListener(v -> showAddCategoryDialog());
    }

    private void setupRecyclerView() {
        recentActivityRecyclerView = findViewById(R.id.recent_activity_list);
        recentActivityRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(recentTasks);
        recentActivityRecyclerView.setAdapter(taskAdapter);
    }

    private void showAddCategoryDialog() {
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        View dialogView = inflater.inflate(R.layout.activity_add_category, null);

        final TextInputEditText etCategoryName = dialogView.findViewById(R.id.et_category_name);
        final TextView tvTaskCount = dialogView.findViewById(R.id.tv_task_count);
        final ImageButton btnDecreaseTasks = dialogView.findViewById(R.id.btn_decrease_tasks);
        final ImageButton btnIncreaseTasks = dialogView.findViewById(R.id.btn_increase_tasks);
        final GridLayout iconGrid = dialogView.findViewById(R.id.icon_grid);
        final Button btnSetDeadline = dialogView.findViewById(R.id.btn_set_deadline);
        final TextView tvSelectedDeadline = dialogView.findViewById(R.id.tv_selected_deadline);

        final int[] iconIds = {
                R.drawable.ic_weightlifting, R.drawable.ic_chart_user, R.drawable.ic_running,
                R.drawable.ic_widgets, R.drawable.ic_chat, R.drawable.ic_school,
                R.drawable.shopping_cart, R.drawable.ic_travel, R.drawable.ic_health,
                R.drawable.ic_finance
        };

        final String[] iconNames = {"Work", "Personal", "Fitness", "Learning", "Interests",
                "Education", "Shopping", "Travel", "Health", "Finance"};
        final int[] selectedIcon = {iconIds[0]};
        final Calendar[] selectedDeadline = {null};
        final int[] taskCount = {1};

        btnDecreaseTasks.setOnClickListener(v -> {
            if (taskCount[0] > 1) {
                taskCount[0]--;
                tvTaskCount.setText(String.valueOf(taskCount[0]));
            }
        });

        btnIncreaseTasks.setOnClickListener(v -> {
            if (taskCount[0] < 10) {
                taskCount[0]++;
                tvTaskCount.setText(String.valueOf(taskCount[0]));
            }
        });

        for (int i = 0; i < iconIds.length; i++) {
            ImageView iconView = new ImageView(this);
            iconView.setImageResource(iconIds[i]);
            iconView.setLayoutParams(new GridLayout.LayoutParams());
            iconView.getLayoutParams().width = dpToPx(48);
            iconView.getLayoutParams().height = dpToPx(48);
            iconView.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));
            final int iconIndex = i;
            iconView.setOnClickListener(v -> {
                selectedIcon[0] = iconIds[iconIndex];
                for (int j = 0; j < iconGrid.getChildCount(); j++) {
                    iconGrid.getChildAt(j).setBackground(null);
                }
                v.setBackground(getResources().getDrawable(R.drawable.icon_selected_background));
            });
            iconGrid.addView(iconView);
        }

        btnSetDeadline.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        selectedDeadline[0] = Calendar.getInstance();
                        selectedDeadline[0].set(year1, monthOfYear, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                        tvSelectedDeadline.setText(sdf.format(selectedDeadline[0].getTime()));
                    }, year, month, day);
            datePickerDialog.show();
        });

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Add New Category");
        dialogBuilder.setPositiveButton("Add", (dialog, i) -> {
            String categoryName = etCategoryName.getText().toString();

            if (!categoryName.isEmpty()) {
                Category newCategory = new Category(categoryName, selectedIcon[0], taskCount[0]);
                newCategory.setDeadline(selectedDeadline[0] != null ? selectedDeadline[0].getTime() : null);
                categories.add(newCategory);
                setupCategoryGrid();
                updateOverallProgress();
                addCompletedTask(categoryName + " Category Added");
            } else {
                Toast.makeText(MainActivity.this, "Please enter a category name", Toast.LENGTH_SHORT).show();
            }
        });

        dialogBuilder.setNegativeButton("Cancel", (dialog, i) -> dialog.dismiss());
        dialogBuilder.create().show();
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    private void setupCategoryGrid() {
        categoryContainer.removeAllViews();

        for (Category category : categories) {
            View categoryView = getLayoutInflater().inflate(R.layout.item_category, categoryContainer, false);

            ImageView iconView = categoryView.findViewById(R.id.category_icon);
            TextView nameView = categoryView.findViewById(R.id.category_name);
            TextView countView = categoryView.findViewById(R.id.task_count);
            TextView deadlineView = categoryView.findViewById(R.id.category_deadline);
            CircularProgressIndicator categoryProgress = categoryView.findViewById(R.id.category_progress);

            iconView.setImageResource(category.getIconResId());
            nameView.setText(category.getName());
            updateCategoryView(category, countView, deadlineView, categoryProgress);

            categoryView.setOnClickListener(v -> showCategoryDetailsDialog(category));

            categoryContainer.addView(categoryView);
        }
    }

    private void updateCategoryView(Category category, TextView countView, TextView deadlineView, CircularProgressIndicator categoryProgress) {
        String taskText = String.format(Locale.getDefault(), "%d/%d Tasks",
                category.getCompletedTasks(), category.getTotalTasks());
        countView.setText(taskText);

        if (category.getDeadline() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            String deadlineText = "Deadline: " + sdf.format(category.getDeadline());
            deadlineView.setText(deadlineText);
            deadlineView.setVisibility(View.VISIBLE);
        } else {
            deadlineView.setVisibility(View.GONE);
        }

        if (categoryProgress != null) {
            categoryProgress.setProgress(category.getProgress());
        }
    }

    private void showCategoryDetailsDialog(Category category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(category.getName());

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_category_details, null);
        TextView taskCountView = dialogView.findViewById(R.id.task_count);
        TextView deadlineView = dialogView.findViewById(R.id.category_deadline);
        Button completeTaskButton = dialogView.findViewById(R.id.btn_complete_task);

        taskCountView.setText(String.format(Locale.getDefault(), "%d/%d Tasks Completed",
                category.getCompletedTasks(), category.getTotalTasks()));

        if (category.getDeadline() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            String deadlineText = "Deadline: " + sdf.format(category.getDeadline());
            deadlineView.setText(deadlineText);
            deadlineView.setVisibility(View.VISIBLE);
        } else {
            deadlineView.setVisibility(View.GONE);
        }

        completeTaskButton.setOnClickListener(v -> {
            category.incrementCompletedTasks();
            updateOverallProgress();
            setupCategoryGrid();
            addCompletedTask("Completed task in " + category.getName());
            taskCountView.setText(String.format(Locale.getDefault(), "%d/%d Tasks Completed",
                    category.getCompletedTasks(), category.getTotalTasks()));
        });

        builder.setView(dialogView);
        builder.setPositiveButton("Close", null);
        builder.create().show();
    }

    private void updateOverallProgress() {
        int totalTasks = 0;
        int completedTasks  = 0;

        for (Category category : categories) {
            totalTasks += category.getTotalTasks();
            completedTasks += category.getCompletedTasks();
        }

        int progress = totalTasks > 0 ? (completedTasks * 100) / totalTasks : 0;
        updateProgress(progress);
    }

    private void updateProgress(int progress) {
        overallProgressIndicator.setProgress(progress);
        progressPercentage.setText(progress + "%");
    }

    private void addCompletedTask(String taskName) {
        String completionTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
        Task newTask = new Task(taskName, completionTime);

        recentTasks.add(0, newTask);
        if (recentTasks.size() > 10) {
            recentTasks = new ArrayList<>(recentTasks.subList(0, 10));
        }

        taskAdapter.updateTasks(recentTasks);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}