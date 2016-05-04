package bf.io.openshop.ux.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import bf.io.openshop.R;
import bf.io.openshop.SettingsMy;
import bf.io.openshop.entities.User;
import bf.io.openshop.entities.drawerMenu.DrawerItemCategory;
import bf.io.openshop.entities.drawerMenu.DrawerItemPage;
import bf.io.openshop.interfaces.DrawerRecyclerInterface;
import bf.io.openshop.listeners.OnSingleClickListener;

/**
 * Adapter handling list of drawer items.
 */
public class DrawerRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM_CATEGORY = 1;
    private static final int TYPE_ITEM_PAGE = 2;

    private final DrawerRecyclerInterface drawerRecyclerInterface;
    private LayoutInflater layoutInflater;
    private Context context;
    private List<DrawerItemCategory> drawerItemCategoryList = new ArrayList<>();
    private List<DrawerItemPage> drawerItemPageList = new ArrayList<>();

    /**
     * Creates an adapter that handles a list of drawer items.
     *
     * @param context                 activity context.
     * @param drawerRecyclerInterface listener indicating events that occurred.
     */
    public DrawerRecyclerAdapter(Context context, DrawerRecyclerInterface drawerRecyclerInterface) {
        this.context = context;
        this.drawerRecyclerInterface = drawerRecyclerInterface;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_ITEM_CATEGORY) {
            View view = layoutInflater.inflate(R.layout.list_item_drawer_category, parent, false);
            return new ViewHolderItemCategory(view, drawerRecyclerInterface);
        } else if (viewType == TYPE_ITEM_PAGE) {
            View view = layoutInflater.inflate(R.layout.list_item_drawer_page, parent, false);
            return new ViewHolderItemPage(view, drawerRecyclerInterface);
        } else {
            View view = layoutInflater.inflate(R.layout.list_item_drawer_header, parent, false);
            return new ViewHolderHeader(view, drawerRecyclerInterface);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolderItemCategory) {
            ViewHolderItemCategory viewHolderItemCategory = (ViewHolderItemCategory) holder;

            DrawerItemCategory drawerItemCategory = getDrawerItem(position);
            viewHolderItemCategory.bindContent(drawerItemCategory);
            viewHolderItemCategory.itemText.setText(drawerItemCategory.getName());
            if (position == 1) {
                viewHolderItemCategory.itemText.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                viewHolderItemCategory.itemText.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, R.drawable.star), null, null, null);
                viewHolderItemCategory.divider.setVisibility(View.VISIBLE);
            } else {
                viewHolderItemCategory.itemText.setTextColor(ContextCompat.getColor(context, R.color.textPrimary));
                viewHolderItemCategory.itemText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                viewHolderItemCategory.divider.setVisibility(View.GONE);
            }
            if (drawerItemCategory.getChildren() == null || drawerItemCategory.getChildren().isEmpty()) {
                viewHolderItemCategory.subMenuIndicator.setVisibility(View.INVISIBLE);
            } else {
                viewHolderItemCategory.subMenuIndicator.setVisibility(View.VISIBLE);
            }
        } else if (holder instanceof ViewHolderItemPage) {
            ViewHolderItemPage viewHolderItemPage = (ViewHolderItemPage) holder;

            DrawerItemPage drawerItemPage = getPageItem(position);
            viewHolderItemPage.bindContent(drawerItemPage);
            viewHolderItemPage.itemText.setText(drawerItemPage.getName());
        } else if (holder instanceof ViewHolderHeader) {
            ViewHolderHeader viewHolderHeader = (ViewHolderHeader) holder;

            User user = SettingsMy.getActiveUser();
            if (user != null) {
                viewHolderHeader.userName.setText(user.getEmail());
            } else {
                viewHolderHeader.userName.setText(context.getString(R.string.Unknown_user));
            }
        }
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        // Clear the animation when the view is detached. Prevent bugs during fast scroll.
        if (holder instanceof ViewHolderItemCategory) {
            ((ViewHolderItemCategory) holder).layout.clearAnimation();
        } else if (holder instanceof ViewHolderItemPage) {
            ((ViewHolderItemPage) holder).layout.clearAnimation();
        }
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        // Apply the animation when the view is attached
        if (holder instanceof ViewHolderItemCategory) {
            setAnimation(((ViewHolderItemCategory) holder).layout);
        } else if (holder instanceof ViewHolderItemPage) {
            setAnimation(((ViewHolderItemPage) holder).layout);
        }
    }

    /**
     * Here is the key method to apply the animation
     */
    private void setAnimation(View viewToAnimate) {
        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
        viewToAnimate.startAnimation(animation);
    }

    // This method returns the number of items present in the list
    @Override
    public int getItemCount() {
        return drawerItemCategoryList.size() + drawerItemPageList.size() + 1; // the number of items in the list will be +1 the titles including the header view.
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_HEADER;
        else if (position <= drawerItemCategoryList.size())
            return TYPE_ITEM_CATEGORY;
        else
            return TYPE_ITEM_PAGE;
    }

    private DrawerItemCategory getDrawerItem(int position) {
        return drawerItemCategoryList.get(position - 1);
    }

    private DrawerItemPage getPageItem(int position) {
        return drawerItemPageList.get(position - drawerItemCategoryList.size() - 1);
    }

    public void addDrawerItemList(List<DrawerItemCategory> drawerItemCategories) {
        if (drawerItemCategories != null)
            drawerItemCategoryList.addAll(drawerItemCategories);
    }

    public void addPageItemList(List<DrawerItemPage> drawerItemPages) {
        if (drawerItemPages != null)
            drawerItemPageList.addAll(drawerItemPages);
    }

    public void addDrawerItem(DrawerItemCategory drawerItemCategory) {
        drawerItemCategoryList.add(drawerItemCategory);

    }

    // Provide a reference to the views for each data item
    public static class ViewHolderItemPage extends RecyclerView.ViewHolder {
        public TextView itemText;
        public View layout;
        private DrawerItemPage drawerItemPage;

        public ViewHolderItemPage(View itemView, final DrawerRecyclerInterface drawerRecyclerInterface) {
            super(itemView);
            itemText = (TextView) itemView.findViewById(R.id.drawer_list_item_text);
            layout = itemView.findViewById(R.id.drawer_list_item_layout);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerRecyclerInterface.onPageSelected(v, drawerItemPage);
                }
            });
        }

        public void bindContent(DrawerItemPage drawerItemPage) {
            this.drawerItemPage = drawerItemPage;
        }
    }

    // Provide a reference to the views for each data item
    public static class ViewHolderItemCategory extends RecyclerView.ViewHolder {
        public TextView itemText;
        public ImageView subMenuIndicator;
        public LinearLayout layout;
        private DrawerItemCategory drawerItemCategory;
        private View divider;

        public ViewHolderItemCategory(View itemView, final DrawerRecyclerInterface drawerRecyclerInterface) {
            super(itemView);
            itemText = (TextView) itemView.findViewById(R.id.drawer_list_item_text);
            subMenuIndicator = (ImageView) itemView.findViewById(R.id.drawer_list_item_indicator);
            layout = (LinearLayout) itemView.findViewById(R.id.drawer_list_item_layout);
            divider = itemView.findViewById(R.id.drawer_list_item_divider);
            itemView.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    drawerRecyclerInterface.onCategorySelected(v, drawerItemCategory);
                }
            });
        }

        public void bindContent(DrawerItemCategory drawerItemCategory) {
            this.drawerItemCategory = drawerItemCategory;
        }
    }

    public static class ViewHolderHeader extends RecyclerView.ViewHolder {
        public TextView userName;

        public ViewHolderHeader(View headerView, final DrawerRecyclerInterface drawerRecyclerInterface) {
            super(headerView);
            userName = (TextView) headerView.findViewById(R.id.navigation_drawer_list_header_text);
            headerView.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    drawerRecyclerInterface.onHeaderSelected();
                }
            });
        }
    }
}
