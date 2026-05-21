//Please don't replace listeners with lambda!

package com.android.support;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.widget.RelativeLayout.ALIGN_PARENT_LEFT;
import static android.widget.RelativeLayout.ALIGN_PARENT_RIGHT;

import org.xml.sax.ErrorHandler;
import android.widget.LinearLayout.LayoutParams;
import android.view.View.OnClickListener;
import android.widget.GridLayout;
import java.util.Objects;

public class Menu {
    //********** Here you can easly change the menu appearance **********//

    //region Variable
    public static final String TAG = "Mod_Menu"; //Tag for logcat

    int TEXT_COLOR = Color.parseColor("#000000");
    int TEXT_COLOR_2 = Color.parseColor("#FFFFFF");
    int TEXT_COLORl = Color.parseColor("#FF4081");
    int TEXT_COLORl_2 = Color.parseColor("#FFFFFF");
    int ACCENT = Color.parseColor("#FF4081");
    int BTN_COLOR = Color.parseColor("#000000");
    int MENU_BG_COLOR = Color.parseColor("#000000"); //#AARRGGBB
    int MENU_FEATURE_BG_COLOR = Color.parseColor("#FFFFFF"); //#AARRGGBB
    int MENU_WIDTH = 260;
    int MENU_HEIGHT = 280;
    int POS_X = 0;
    int POS_Y = 100;

    int MENU_CORNER = 10;
    int ICON_SIZE = 55; //Change both width and height of image
    float ICON_ALPHA = 1f; //Transparent

    int BtnON = Color.parseColor("#1b5e20");
    int BtnOFF = Color.parseColor("#7f0000");
    int CategoryBG = Color.parseColor("#2F3D4C");
    int SeekBarColor = Color.parseColor("#000000");
    int SeekBarProgressColor = Color.parseColor("#000000");
    int CheckBoxColor = Color.parseColor("#000000");
    int RadioColor = Color.parseColor("#FFFFFF");
    int GridColor = Color.parseColor("#E0E0E0");
    int ToggleTrackON = Color.parseColor("#AA000000");
    int ToggleTrackOFF = Color.parseColor("#E0E0E0");
    int ToggleThumbOFF = Color.parseColor("#E0E0E0");
	int ToggleThumbON = Color.BLACK;
    //********************************************************************//

    RelativeLayout mCollapsed, mRootContainer;
    LinearLayout mExpanded, mods, mSettings, mCollapse;
    LinearLayout.LayoutParams scrlLLExpanded, scrlLL;
    WindowManager mWindowManager;
    WindowManager.LayoutParams vmParams;
    ImageView startimage;
    FrameLayout rootFrame;
    ScrollView scrollView;
    boolean stopChecking, overlayRequired;
    Context getContext;
    TextView title, icon_back, categorytitle, categorymaintext;
    LinearLayout linearCategoryHead;
    LinearLayout[] mTab = new LinearLayout[10];
    int Index;

    //initialize methods from the native library
    native void Init(Context context, TextView title, TextView subTitle);

    native String Icon();

    native String IconWebViewData();

    native String[] GetFeatureList();

    native boolean IsGameLibLoaded();

    native String[] GetShaderList();

    native void ClearShaders();

    native void SetCurrentShader(String name);

    native String GetCurrentShader();

    native void SetCaptureMode(boolean on);

    native boolean IsCaptureMode();

    native String GetTargetLibrary();

    native void SetTargetLibrary(String name);

    native boolean IsTargetLibraryLoaded();

    // Theme accent used by Buttons/ColorPicker/Shader Inspector
    int color() {
        return ACCENT;
    }

    // Floating shader inspector state
    WindowManager.LayoutParams shaderInspectorParams;
    View shaderInspectorRoot;
    boolean shaderInspectorShown;
    LinearLayout shaderListContainer;
    TextView shaderInspectorTitle;
    EditText shaderSearchBox;
    Handler shaderRefreshHandler;
    Runnable shaderRefreshRunnable;
    String shaderSearchQuery = "";

    // Library status (settings tab)
    TextView libStatusView;
    Handler libStatusHandler;
    Runnable libStatusRunnable;

    //Here we write the code for our Menu
    // Reference: https://www.androidhive.info/2016/11/android-floating-widget-like-facebook-chat-head/
    public Menu(Context context) {

        getContext = context;
        Preferences.context = context;
        rootFrame = new FrameLayout(context); // Global markup
        rootFrame.setOnTouchListener(onTouchListener());
        mRootContainer = new RelativeLayout(context); // Markup on which two markups of the icon and the menu itself will be placed
        mCollapsed = new RelativeLayout(context); // Markup of the icon (when the menu is minimized)
        mCollapsed.setVisibility(View.VISIBLE);
        mCollapsed.setAlpha(ICON_ALPHA);

        //********** The box of the mod menu **********
        mExpanded = new LinearLayout(context); // Menu markup (when the menu is expanded)
        mExpanded.setVisibility(View.GONE);
        mExpanded.setBackgroundColor(MENU_BG_COLOR);
        mExpanded.setOrientation(LinearLayout.VERTICAL);
        // mExpanded.setPadding(1, 1, 1, 1); //So borders would be visible
        mExpanded.setLayoutParams(new LinearLayout.LayoutParams(dp(MENU_WIDTH), WRAP_CONTENT));
        GradientDrawable gdMenuBody = new GradientDrawable();
        gdMenuBody.setCornerRadius(dp(MENU_CORNER)); //Set corner
        gdMenuBody.setColor(MENU_BG_COLOR); //Set background color
        mExpanded.setBackground(gdMenuBody); //Apply GradientDrawable to it

        //********** The icon to open mod menu **********
        startimage = new ImageView(context);
        startimage.setLayoutParams(new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        int applyDimension = (int) TypedValue.applyDimension(1, ICON_SIZE, context.getResources().getDisplayMetrics()); //Icon size
        startimage.getLayoutParams().height = applyDimension;
        startimage.getLayoutParams().width = applyDimension;
        //startimage.requestLayout();
        startimage.setScaleType(ImageView.ScaleType.FIT_XY);
        byte[] decode = Base64.decode(Icon(), 0);
        startimage.setImageBitmap(BitmapFactory.decodeByteArray(decode, 0, decode.length));
        ((ViewGroup.MarginLayoutParams) startimage.getLayoutParams()).topMargin = convertDipToPixels(10);
        //Initialize event handlers for buttons, etc.
        startimage.setOnTouchListener(onTouchListener());
        startimage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mCollapsed.setVisibility(View.GONE);
                mExpanded.setVisibility(View.VISIBLE);
            }
        });

        //********** The icon in Webview to open mod menu **********
        WebView wView = new WebView(context); //Icon size width=\"50\" height=\"50\"
        wView.setLayoutParams(new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        int applyDimension2 = (int) TypedValue.applyDimension(1, ICON_SIZE, context.getResources().getDisplayMetrics()); //Icon size
        wView.getLayoutParams().height = applyDimension2;
        wView.getLayoutParams().width = applyDimension2;
        wView.loadData("<html>" +
                "<head></head>" +
                "<body style=\"margin: 0; padding: 0\">" +
                "<img src=\"" + IconWebViewData() + "\" width=\"" + ICON_SIZE + "\" height=\"" + ICON_SIZE + "\" >" +
                "</body>" +
                "</html>", "text/html", "utf-8");
        wView.setBackgroundColor(0x00000000); //Transparent
        wView.setAlpha(ICON_ALPHA);
        wView.getSettings().setAppCacheEnabled(true);
        wView.setOnTouchListener(onTouchListener());

        //********** Settings icon **********
        TextView settings = new TextView(context); //Android 5 can't show ⚙, instead show other icon instead
        settings.setText(Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M ? "⚙" : "\uD83D\uDD27");
        settings.setTextColor(TEXT_COLOR);
        settings.setTypeface(Typeface.DEFAULT_BOLD);
        settings.setTextSize(20.0f);
        RelativeLayout.LayoutParams rlsettings = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        rlsettings.addRule(ALIGN_PARENT_RIGHT);
        settings.setLayoutParams(rlsettings);
        settings.setOnClickListener(new View.OnClickListener() {
            boolean settingsOpen;

            @Override
            public void onClick(View v) {
                try {
                    settingsOpen = !settingsOpen;
                    if (settingsOpen) {
                        scrollView.removeView(mods);
                        scrollView.addView(mSettings);
                        scrollView.scrollTo(0, 0);
                    } else {
                        scrollView.removeView(mSettings);
                        scrollView.addView(mods);
                    }
                } catch (IllegalStateException e) {
                }
            }
        });

        //********** Settings **********
        mSettings = new LinearLayout(context);
        mSettings.setOrientation(LinearLayout.VERTICAL);
       // featureList(SettingsList(), mSettings);

        //********** Title **********
        RelativeLayout relativeLayout = new RelativeLayout(getContext);
        relativeLayout.setPadding(dp(2), dp(2), dp(2), dp(2));

        title = new TextView(context);
        title.setTextColor(TEXT_COLOR_2);
        title.setTextSize(23.0f);
        title.setTypeface(Typeface.createFromAsset(context.getAssets(), "black.mods"));
        title.setGravity(Gravity.CENTER);
        RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        rl.addRule(RelativeLayout.CENTER_HORIZONTAL);
        title.setLayoutParams(rl);

        linearCategoryHead = new LinearLayout(context);
        linearCategoryHead.setOrientation(0);
        linearCategoryHead.setGravity(16);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-2, -2);
        layoutParams.addRule(5);
        linearCategoryHead.setLayoutParams(layoutParams);
        linearCategoryHead.setVisibility(8);

        icon_back = new TextView(context);
        icon_back.setTextColor(TEXT_COLOR_2);
        icon_back.setTextSize(30.0f);
        icon_back.setPadding(dp(2), dp(2), dp(2), dp(2));
        icon_back.setTypeface(Typeface.createFromAsset(context.getAssets(), "mods.black"));
        icon_back.setText("west");

        categorytitle = new TextView(context);
        categorytitle.setTextColor(TEXT_COLOR_2);
        categorytitle.setTextSize(18.0f);

        LayoutParams layoutParams2 = new LayoutParams(-2, -2);
        layoutParams2.setMarginStart(dp(5));

        categorytitle.setLayoutParams(layoutParams2);
        categorytitle.setTypeface(Typeface.SERIF, 1);
        categorytitle.setGravity(17);

        TextView textView = new TextView(context);
        textView.setTextColor(TEXT_COLOR_2);
        textView.setTextSize(30.0f);
        textView.setPadding(dp(2), dp(2), dp(2), dp(2));
        textView.setTypeface(Typeface.createFromAsset(context.getAssets(), "mods.black"));
        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(-2, -2);
        layoutParams3.addRule(11);
        textView.setLayoutParams(layoutParams3);
        textView.setText("cancel");
        textView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    mCollapsed.setVisibility(View.VISIBLE);
                    mCollapsed.setAlpha(ICON_ALPHA);
                    mExpanded.setVisibility(View.GONE);
                }
            });

        TextView textView2 = new TextView(context);
        textView2.setTextColor(TEXT_COLOR_2);
        textView2.setTextSize(11.0f);
        textView2.setGravity(17);
        textView2.setPadding(dp(3), dp(3), dp(3), dp(3));
        textView2.setTypeface(Typeface.SERIF);

        //********** Mod menu feature list **********
        scrollView = new ScrollView(context);
        //Auto size. To set size manually, change the width and height example 500, 500
        scrlLL = new LinearLayout.LayoutParams(MATCH_PARENT, dp(MENU_HEIGHT));
        scrlLLExpanded = new LinearLayout.LayoutParams(mExpanded.getLayoutParams());
        scrlLLExpanded.weight = 1.0f;
        scrollView.setLayoutParams(Preferences.isExpanded ? scrlLLExpanded : scrlLL);
        scrollView.setBackgroundColor(MENU_FEATURE_BG_COLOR);
        mods = new LinearLayout(context);
        mods.setOrientation(LinearLayout.VERTICAL);

        //********** Adding view components **********
        mRootContainer.addView(mCollapsed);
        mRootContainer.addView(mExpanded);
        if (IconWebViewData() != null) {
            mCollapsed.addView(wView);
        } else {
            mCollapsed.addView(startimage);
        }
        relativeLayout.addView(title);
        linearCategoryHead.addView(icon_back);
        linearCategoryHead.addView(categorytitle);
        relativeLayout.addView(linearCategoryHead);
        relativeLayout.addView(textView);
        mExpanded.addView(relativeLayout);
        scrollView.addView(mods);
        mExpanded.addView(scrollView);
        mExpanded.addView(textView2);

        Init(context, title, textView2);
    }

    public void ShowMenu() {
        rootFrame.addView(mRootContainer);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            boolean viewLoaded = false;

            @Override
            public void run() {
                //If the save preferences is enabled, it will check if game lib is loaded before starting menu
                //Comment the if-else code out except startService if you want to run the app and test preferences
                if (Preferences.loadPref && !IsGameLibLoaded() && !stopChecking) {
                    if (!viewLoaded) {
                        Category(mods, true, "Save preferences was been enabled. Waiting for game lib to be loaded...\n\nForce load menu may not apply mods instantly. You would need to reactivate them again");
                        Button(mods, -100, "Force load menu");
                        viewLoaded = true;
                    }
                    handler.postDelayed(this, 600);
                } else {
                    mods.removeAllViews();
                    featureList(GetFeatureList(), mods);
                }
            }
        }, 500);
    }

    @SuppressLint("WrongConstant")
    public void SetWindowManagerWindowService() {
        //Variable to check later if the phone supports Draw over other apps permission
        int iparams = Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ? 2038 : 2002;
        vmParams = new WindowManager.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, iparams, 8, -3);
        //params = new WindowManager.LayoutParams(WindowManager.LayoutParams.LAST_APPLICATION_WINDOW, 8, -3);
        vmParams.gravity = 51;
        vmParams.x = POS_X;
        vmParams.y = POS_Y;

        mWindowManager = (WindowManager) getContext.getSystemService(getContext.WINDOW_SERVICE);
        mWindowManager.addView(rootFrame, vmParams);

        overlayRequired = true;
    }

    @SuppressLint("WrongConstant")
    public void SetWindowManagerActivity() {
        vmParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                POS_X,//initialX
                POS_Y,//initialy
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_OVERSCAN |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                        WindowManager.LayoutParams.FLAG_SPLIT_TOUCH,
                PixelFormat.TRANSPARENT
        );
        vmParams.gravity = 51;
        vmParams.x = POS_X;
        vmParams.y = POS_Y;

        mWindowManager = ((Activity) getContext).getWindowManager();
        mWindowManager.addView(rootFrame, vmParams);
    }

    private View.OnTouchListener onTouchListener() {
        return new View.OnTouchListener() {
            final View collapsedView = mCollapsed;
            final View expandedView = mExpanded;
            private float initialTouchX, initialTouchY;
            private int initialX, initialY;

            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = vmParams.x;
                        initialY = vmParams.y;
                        initialTouchX = motionEvent.getRawX();
                        initialTouchY = motionEvent.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        int rawX = (int) (motionEvent.getRawX() - initialTouchX);
                        int rawY = (int) (motionEvent.getRawY() - initialTouchY);
                        mExpanded.setAlpha(1f);
                        mCollapsed.setAlpha(1f);
                        //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                        //So that is click event.
                        if (rawX < 10 && rawY < 10 && isViewCollapsed()) {
                            //When user clicks on the image view of the collapsed layout,
                            //visibility of the collapsed layout will be changed to "View.GONE"
                            //and expanded view will become visible.
                            try {
                                collapsedView.setVisibility(View.GONE);
                                expandedView.setVisibility(View.VISIBLE);
                            } catch (NullPointerException e) {

                            }
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        mExpanded.setAlpha(0.5f);
                        mCollapsed.setAlpha(0.5f);
                        //Calculate the X and Y coordinates of the view.
                        vmParams.x = initialX + ((int) (motionEvent.getRawX() - initialTouchX));
                        vmParams.y = initialY + ((int) (motionEvent.getRawY() - initialTouchY));
                        //Update the layout with new X & Y coordinate
                        mWindowManager.updateViewLayout(rootFrame, vmParams);
                        return true;
                    default:
                        return false;
                }
            }
        };
    }

    private void featureList(String[] listFT, LinearLayout linearLayout) {
        //Currently looks messy right now. Let me know if you have improvements
        int featNum, subFeat = 0;
        LinearLayout llBak = linearLayout;

        for (int i = 0; i < listFT.length; i++) {
            boolean switchedOn = false;
            //Log.i("featureList", listFT[i]);
            String feature = listFT[i];
            if (feature.contains("__True")) {
                switchedOn = true;
                feature = feature.replaceFirst("__True", "");
            }

            linearLayout = llBak;
            if (feature.contains("CollapseAdd_")) {
                //if (collapse != null)
                linearLayout = mCollapse;
                feature = feature.replaceFirst("CollapseAdd_", "");
            }
            String[] str = feature.split("__");

            //Assign feature number
            if (TextUtils.isDigitsOnly(str[0]) || str[0].matches("-[0-9]*")) {
                featNum = Integer.parseInt(str[0]);
                feature = feature.replaceFirst(str[0] + "__", "");
                subFeat++;
            } else {
                //Subtract feature number. We don't want to count ButtonLink, Category, RichTextView and RichWebView
                featNum = i - subFeat;
            }
            
            if (feature.contains("TabAdd__")) {
                int Int = Integer.parseInt(feature.split("__")[1]);
                linearLayout = mTab[Int];
                feature = feature.replaceFirst("TabAdd__" + Int+ "__", "");
            }
            
            String[] strSplit = feature.split("__");
            switch (strSplit[0]) {
                case "Toggle":
                    Switch(linearLayout, featNum, strSplit[1], switchedOn);
                    break;
                case "SeekBar":
                    SeekBar(linearLayout, featNum, strSplit[1], Integer.parseInt(strSplit[2]), Integer.parseInt(strSplit[3]));
                    break;
                case "Button":
                    Button(linearLayout, featNum, strSplit[1]);
                    break;
                case "Buttons":
                    Buttons(linearLayout, featNum, strSplit[1]);
                    break;
                case "ButtonOnOff":
                    ButtonOnOff(linearLayout, featNum, strSplit[1], switchedOn);
                    break;
                case "Spinner":
                    TextView(linearLayout, strSplit[1]);
                    Spinner(linearLayout, featNum, strSplit[1], strSplit[2], strSplit[3]);
                    break;
                case "InputText":
                    InputText(linearLayout, featNum, strSplit[1]);
                    break;
                case "InputValue":
                    if (strSplit.length == 3)
                        InputNum(linearLayout, featNum, strSplit[2], Integer.parseInt(strSplit[1]));
                    if (strSplit.length == 2)
                        InputNum(linearLayout, featNum, strSplit[1], 0);
                    break;
                case "CheckBox":
                    CheckBox(linearLayout, featNum, strSplit[1], switchedOn);
                    break;
                case "RadioButton":
                    RadioButton(linearLayout, featNum, strSplit[1], strSplit[2]);
                    break;
                case "Collapse":
                    Collapse(linearLayout, strSplit[1], switchedOn);
                    subFeat++;
                    break;
                case "ButtonLink":
                    subFeat++;
                    ButtonLink(linearLayout, strSplit[1], strSplit[2]);
                    break;
                case "Category":
                    subFeat++;
                    Category(linearLayout, switchedOn, strSplit[1]);
                    break;
                case "RichTextView":
                    subFeat++;
                    if (featNum == -11) {
                        addLibraryStatusView(linearLayout);
                    } else {
                        TextView(linearLayout, strSplit[1]);
                    }
                    break;
                case "RichWebView":
                    subFeat++;
                    WebTextView(linearLayout, strSplit[1]);
                    break;
                case "GridViewLayout":
                    GridViewLayout(linearLayout, strSplit[1].split(","), strSplit[2].split(","));
                    break;
                case "ArrayBox":
                    linearLayout.addView(ArrayBox(featNum, strSplit[1], switchedOn));
                    break;
                case "ColorPicker":
					linearLayout.addView(ColorPicker(featNum, strSplit[1], Integer.parseInt(strSplit[2]), Integer.parseInt(strSplit[3]),Integer.parseInt(strSplit[4]), Integer.parseInt(strSplit[5])));
					break;
            }
        }
    }

    private void Buttons(LinearLayout linearLayout, final int featNum, final String featName) {
		String[] split = featName.split(",");
		int buttonsPerRow = split.length >= 4 ? 2 : split.length; // 2 buttons per row if 4+, else all in one row

		LinearLayout currentRow = null;

		for (int i2 = 0; i2 < split.length; i2++) {
			// Create new row when needed
			if (i2 % buttonsPerRow == 0) {
				currentRow = new LinearLayout(this.getContext);
				currentRow.setOrientation(0); // Horizontal layout
				linearLayout.addView(currentRow);
			}

			Button button = new Button(this.getContext);

			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -1);
			layoutParams.setMargins(dp(5), dp(8), dp(5), dp(8));
			layoutParams.weight = 1.0f;
			button.setLayoutParams(layoutParams);
			button.setTextSize(11.0f);
			button.setText(split[i2]);
			button.setTextColor(TEXT_COLOR_2);
			button.setAllCaps(false);

			GradientDrawable gradientDrawable = new GradientDrawable();
			gradientDrawable.setColor(TEXT_COLOR);
			gradientDrawable.setCornerRadius((float) dp(20));
			button.setBackground(gradientDrawable);

			final int buttonIndex = i2;
			button.setOnClickListener(new View.OnClickListener() {
					public void onClick(View view) {
						// Special-case: feat 21 = "Show Shaders, Clear Shaders" -> Java handles index 0
						if (featNum == 21 && buttonIndex == 0) {
							showShaderInspector();
							return;
						}
						Preferences.changeFeatureInt(featName, featNum, buttonIndex);
					}
				});
			currentRow.addView(button);
		}
	}

    private View ColorPicker(final int featNum, final String featName, final int alpha, final int red, final int green, final int blue) {
		final LinearLayout linearLayout = new LinearLayout(getContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        layoutParams.setMargins(20, 10, 20, 10);
		linearLayout.setOrientation(0);
		linearLayout.setGravity(16);
		linearLayout.setPadding(10, 8, 8, 8);
		GradientDrawable gdLayout = new GradientDrawable();
        gdLayout.setColor(Color.argb(0, 0, 0, 0));
        gdLayout.setCornerRadii(new float[]{60, 60, 0, 0, 60, 60, 0, 0});
        gdLayout.setStroke(4, color(), 5, 5);
        linearLayout.setBackground(gdLayout);
		linearLayout.setLayoutParams(layoutParams);

		LayoutParams lp = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lp.weight = 1.0f;

		LinearLayout linearLayout4 = new LinearLayout(getContext);
		linearLayout4.setLayoutParams(lp);
		linearLayout4.setGravity(8388611);

		LinearLayout linearLayout2 = new LinearLayout(getContext);
		linearLayout2.setOrientation(1);

		TextView textView = new TextView(getContext);
		textView.setTextColor(TEXT_COLOR);
		textView.setText(featName);
		textView.setTextSize(14.0F);
		textView.setGravity(3);
		textView.setPadding(20, 0, 10, 0);

		LinearLayout linearLayout3 = new LinearLayout(getContext);
		linearLayout3.setOrientation(0);
		linearLayout3.setPadding(20, 5, 0, 5);
		linearLayout3.setGravity(16);

		final int[] initialProgress = new int[]{alpha, red, green, blue};

		final View colorView = new View(getContext);
		colorView.setLayoutParams(new LayoutParams(dp(20), dp(20)));
		colorView.setBackgroundColor(Color.argb(alpha, red, green, blue));

		final TextView colorTextView = new TextView(getContext);
        colorTextView.setText(("#" + String.format("%02x", new Object[]{new Integer(alpha)}) + String.format("%02x", new Object[]{new Integer(red)}) + String.format("%02x", new Object[]{new Integer(green)}) + String.format("%02x", new Object[]{new Integer(blue)})).toUpperCase());
		colorTextView.setTextColor(TEXT_COLOR);
		colorTextView.setTextSize(14.0F);
		colorTextView.setGravity(3);
		colorTextView.setPadding(5, 0, 0, 0);

        final Button button = new Button(getContext);
        button.setText("Pick");
		GradientDrawable gd = new GradientDrawable();
        gd.setColor(TEXT_COLORl_2);
        gd.setCornerRadii(new float[]{60, 60, 0, 0, 60, 60, 0, 0});
        gd.setStroke(3, TEXT_COLOR, 0, 0);
        button.setBackground(gd);
        button.setTextColor(TEXT_COLOR);
        button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					final AlertDialog alert = new AlertDialog.Builder(getContext, 2).create();
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
						Objects.requireNonNull(alert.getWindow()).setType(Build.VERSION.SDK_INT >= 26 ? 2038 : 2002);
					}
					alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
							public void onCancel(DialogInterface dialog) {
								InputMethodManager imm = (InputMethodManager) getContext.getSystemService(getContext.INPUT_METHOD_SERVICE);
								imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
							}
						});

					LinearLayout linearLayout1 = new LinearLayout(getContext);
					linearLayout1.setPadding(5, 5, 5, 5);
					linearLayout1.setOrientation(LinearLayout.VERTICAL);
					GradientDrawable gdLayout2 = new GradientDrawable();
					gdLayout2.setColor(TEXT_COLORl_2);
					gdLayout2.setStroke(3, TEXT_COLOR);
					linearLayout1.setBackground(gdLayout2);

					final TextView titleText = new TextView(getContext);
					titleText.setText(Html.fromHtml(new StringBuffer().append(new StringBuffer().append("<u>").append(featName).toString()).append("</u>").toString()));
					titleText.setGravity(Gravity.CENTER);
					titleText.setTextColor(TEXT_COLOR);
					titleText.setTextSize(22f);
					titleText.setTypeface(Typeface.DEFAULT_BOLD);

					LinearLayout previewLayout = new LinearLayout(getContext);
					previewLayout.setLayoutParams(new LayoutParams(MATCH_PARENT, WRAP_CONTENT));
					previewLayout.setGravity(Gravity.CENTER_VERTICAL);
					previewLayout.setPadding(15, 10, 15, 10);
					previewLayout.setOrientation(0);

					final View colorview2 = new View(getContext);
					colorview2.setLayoutParams(new LayoutParams(dp(20),dp(20)));
					colorview2.setBackgroundColor(Color.argb(initialProgress[0], initialProgress[1], initialProgress[2], initialProgress[3]));

					final TextView colorTextView2 = new TextView(getContext);
				    colorTextView2.setText(("#" + String.format("%02x", new Object[]{new Integer(initialProgress[0])}) + String.format("%02x", new Object[]{new Integer(initialProgress[1])}) + String.format("%02x", new Object[]{new Integer(initialProgress[2])}) + String.format("%02x", new Object[]{new Integer(initialProgress[3])})).toUpperCase());
					colorTextView2.setTextColor(TEXT_COLOR);
					colorTextView2.setPadding(5, 0, 0, 0);
					colorTextView2.setTextSize(20f);

					final TextView copytextView = new TextView(getContext);
					copytextView.setText("❏ Copy Code");
					copytextView.setTextColor(TEXT_COLOR);
					copytextView.setTextSize(15f);
					copytextView.setPadding(175, 0, 0, 0);
					copytextView.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View p1) {
								((ClipboardManager) getContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText(null, colorTextView2.getText()));
								copytextView.setText("✓ Copied!");
								final Handler handler = new Handler();
								handler.postDelayed(new Runnable() {
										@Override
										public void run() {
											copytextView.setText("❏ Copy Code");
										}
									}, 1500);
							}
						});

					LinearLayout seekLayout = new LinearLayout(getContext);
					seekLayout.setLayoutParams(new LayoutParams(MATCH_PARENT, WRAP_CONTENT));
					seekLayout.setPadding(10,0,0,0);
					seekLayout.setOrientation(1);

					final String[] strArr = new String[]{"Transparency", "Red", "Green", "Blue"};
					final SeekBar[] seekBarArr = new SeekBar[4];
					int[] iArr = new int[]{TEXT_COLORl, Color.RED, Color.GREEN, Color.BLUE};
					for (int i = 0; i < 4; i++) {
						LinearLayout linearLayout3 = new LinearLayout(getContext);
						linearLayout3.setOrientation(1);
						linearLayout3.setGravity(17);
						final TextView textView3 = new TextView(getContext);
						textView3.setText(new StringBuffer().append(new StringBuffer().append(new StringBuffer().append(strArr[i]).append(" (").toString()).append(initialProgress[i]).toString()).append(") ").toString());
						textView3.setTextColor(iArr[i]);
						textView3.setGravity(8388611);
						textView3.setPadding(10, 0, 0, 0);
					    SeekBar seekBar = new SeekBar(getContext);
						seekBar.setLayoutParams(new LayoutParams(-1, -2));
						seekBar.setMax(255);
						seekBar.setProgress(initialProgress[i]);
						seekBar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(iArr[i], PorterDuff.Mode.MULTIPLY));
						seekBar.getThumb().setColorFilter(iArr[i], PorterDuff.Mode.SRC_IN);
						final int xg = i;
						seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

								@Override
								public void onProgressChanged(SeekBar p1, int p2, boolean p3) {
									int alpha = seekBarArr[0].getProgress();
									int red = seekBarArr[1].getProgress();
									int green = seekBarArr[2].getProgress();
									int blue = seekBarArr[3].getProgress();
									int bg = Color.argb(alpha, red, green, blue);
									colorview2.setBackgroundColor(bg);
									colorTextView2.setText(("#" + String.format("%02x", new Object[]{new Integer(alpha)}) + String.format("%02x", new Object[]{new Integer(red)}) + String.format("%02x", new Object[]{new Integer(green)}) + String.format("%02x", new Object[]{new Integer(blue)})).toUpperCase());
									textView3.setText(strArr[xg] + " (" + p2 + ") ");
								}

								@Override
								public void onStartTrackingTouch(SeekBar p1) {
								}

								@Override
								public void onStopTrackingTouch(SeekBar p1) {
								}
							});
						linearLayout3.addView(textView3);
						linearLayout3.addView(seekBar);
						seekLayout.addView(linearLayout3);
						seekBarArr[i] = seekBar;
					}

					RelativeLayout relativeLayout = new RelativeLayout(getContext);
					relativeLayout.setPadding(0, 0, 0, 0);
					relativeLayout.setVerticalGravity(Gravity.CENTER);

					RelativeLayout.LayoutParams forright = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
					forright.addRule(ALIGN_PARENT_RIGHT);

					final Button btndialog = new Button(getContext);
					btndialog.setLayoutParams(forright);
					GradientDrawable gdBtnDialog = new GradientDrawable();
					gdBtnDialog.setColor(TEXT_COLORl_2);
					gdBtnDialog.setCornerRadii(new float[] {30,30,0,0,0,0,0,0});
					gdBtnDialog.setStroke(3, TEXT_COLOR, 0, 0);
					btndialog.setBackground(gdBtnDialog);
					btndialog.setTextColor(TEXT_COLOR);
					btndialog.setPadding(15,10,15,10);
					btndialog.setText("SET VALUE");
					btndialog.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								int intAlpha = seekBarArr[0].getProgress();
								int intRed = seekBarArr[1].getProgress();
								int intGreen = seekBarArr[2].getProgress();
								int intBlue = seekBarArr[3].getProgress();
								for (int i = 0; i < 4; i++) {
									initialProgress[i] = seekBarArr[i].getProgress();
                                }
								int bg = Color.argb(intAlpha, intRed, intGreen, intBlue);
						        colorView.setBackgroundColor(bg);

								Preferences.ChangeFeatureColor(featName, featNum, intAlpha, intRed, intGreen, intBlue);
						    	colorTextView.setText(("#" + String.format("%02x", new Object[]{new Integer(intAlpha)}) + String.format("%02x", new Object[]{new Integer(intRed)}) + String.format("%02x", new Object[]{new Integer(intGreen)}) + String.format("%02x", new Object[]{new Integer(intBlue)})).toUpperCase());
								alert.dismiss();
							}
						});

					RelativeLayout.LayoutParams forleft = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
					forleft.addRule(ALIGN_PARENT_LEFT);

					final Button btndialog2 = new Button(getContext);
					btndialog2.setLayoutParams(forleft);
					GradientDrawable gdBtnDialog2 = new GradientDrawable();
					gdBtnDialog2.setColor(TEXT_COLORl_2);
					gdBtnDialog2.setCornerRadii(new float[] {0,0,30,30,0,0,0,0});
					gdBtnDialog2.setStroke(3, TEXT_COLOR);
					btndialog2.setText("CANCEL");
					btndialog2.setBackground(gdBtnDialog2);
					btndialog2.setTextColor(TEXT_COLOR);
					btndialog2.setPadding(15, 10, 15, 10);
					btndialog2.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								alert.cancel();
							}
						});

					linearLayout1.addView(titleText);
					linearLayout1.addView(previewLayout);
					previewLayout.addView(colorview2);
					previewLayout.addView(colorTextView2);
					previewLayout.addView(copytextView);
					linearLayout1.addView(seekLayout);
					linearLayout1.addView(relativeLayout);
					relativeLayout.addView(btndialog);
					relativeLayout.addView(btndialog2);
					alert.setView(linearLayout1);
					alert.show();
				}
			});
	    linearLayout.addView(linearLayout4);
		linearLayout4.addView(linearLayout2);
		linearLayout2.addView(textView);
        linearLayout2.addView(linearLayout3);
        linearLayout3.addView(colorView);
		linearLayout3.addView(colorTextView);
        linearLayout.addView(button);
        return linearLayout;
    }

    private View ArrayBox(final int featNum, final String featName, final boolean switchedOn) {
        LinearLayout mainLayout = new LinearLayout(getContext);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams mainParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        mainParams.setMargins(dp(2), dp(2), dp(2), dp(2));
        mainLayout.setLayoutParams(mainParams);

        final String[] list = featName.split(",");
        final int itemsPerLine = 2;

        LinearLayout currentLineLayout = null;

        for (int i = 0; i < list.length; i++) {
            if (i % itemsPerLine == 0) {
                currentLineLayout = new LinearLayout(getContext);
                currentLineLayout.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
                currentLineLayout.setLayoutParams(lineParams);
                mainLayout.addView(currentLineLayout);
            }

            final int u = i;
            final CheckBox checkbox = new CheckBox(getContext);
            checkbox.setText(list[i]);
			checkbox.setTextSize(11f);
            checkbox.setTextColor(TEXT_COLOR);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                checkbox.setButtonTintList(ColorStateList.valueOf(color()));
            }

            checkbox.setChecked(Preferences.loadPrefBool(list[i], featNum + u, switchedOn));
            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						Preferences.changeFeatureBool(list[u], featNum + u, isChecked);
					}
				});

            LinearLayout.LayoutParams cbParams = new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1.0f);
            checkbox.setLayoutParams(cbParams);

            if (currentLineLayout != null) {
                currentLineLayout.addView(checkbox);
            }
        }
        
        int remainder = list.length % itemsPerLine;
        if (remainder != 0 && currentLineLayout != null) {
            for (int j = 0; j < (itemsPerLine - remainder); j++) {
                View dummyView = new View(getContext);
                LinearLayout.LayoutParams dummyParams = new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1.0f);
                dummyView.setLayoutParams(dummyParams);
                currentLineLayout.addView(dummyView);
            }
        }

        return mainLayout;
    }
    
    private void GridViewLayout(LinearLayout linearLayout, final String[] featName, final String[] featName2) {
        LinearLayout linearLayout2 = new LinearLayout(getContext);
        linearLayout2.setOrientation(1);

        final GridLayout gridLayout = new GridLayout(getContext);
        gridLayout.setLayoutParams(new LayoutParams(-1, -1));
        gridLayout.setRowCount((featName.length / 2) + (featName.length % 2));
        gridLayout.setColumnCount(2);

        final LinearLayout[] linearLayoutArr = new LinearLayout[featName.length];

        LinearLayout linearLayout3 = new LinearLayout(this.getContext);
        linearLayout3.setOrientation(1);

        for (int i = 0; i < featName.length; i++) {
            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
            layoutParams.width = 0;
            layoutParams.height = -2;
            layoutParams.columnSpec = GridLayout.spec(Integer.MIN_VALUE, 1.0f);
            layoutParams.setMargins(dp(4), dp(4), dp(4), dp(4));

            LinearLayout linearLayout4 = new LinearLayout(this.getContext);
            linearLayout4.setOrientation(1);
            linearLayout4.setGravity(17);
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setColor(GridColor);
            gradientDrawable.setCornerRadius((float) dp(20));
            linearLayout4.setBackgroundDrawable(gradientDrawable);
            linearLayout4.setLayoutParams(layoutParams);
            linearLayout4.setPadding(dp(7), dp(4), dp(4), dp(4));

            LinearLayout linearLayout5 = new LinearLayout(this.getContext);
            linearLayout5.setOrientation(0);
            linearLayout5.setGravity(16);

            TextView textView = new TextView(getContext);
            textView.setTextColor(TEXT_COLOR);
            textView.setTextSize(40.0f);
            textView.setPadding(dp(2), dp(2), dp(2), dp(2));
            textView.setText(featName2[i]);
            textView.setTypeface(Typeface.createFromAsset(getContext.getAssets(), "mods.black"));

            final TextView textView2 = new TextView(getContext);
            textView2.setTextColor(TEXT_COLOR);
            textView2.setTextSize(15.0f);
            textView2.setPadding(dp(2), 0, dp(2), 0);
            textView2.setTypeface(Typeface.DEFAULT_BOLD);
            textView2.setGravity(17);
            textView2.setText(featName[i]);

            LayoutParams layoutParams2 = new LayoutParams(-1, -2);
            layoutParams2.setMargins(0, dp(4), 0, 0);

            final Button button = new Button(this.getContext);
            button.setText("VIEW");
            button.setTextColor(TEXT_COLOR_2);
            button.setLayoutParams(layoutParams2);
            GradientDrawable gradientDrawable2 = new GradientDrawable();
            gradientDrawable2.setColor(TEXT_COLOR);
            gradientDrawable2.setCornerRadius(dp(20));
            button.setBackground(gradientDrawable2);
            final int i2;
            i2  = i;
            button.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        button.setEnabled(false);
                        Index = i2;
                        String replaceAll = featName[i2];
                        categorytitle.setText(replaceAll);
                        categorymaintext.setText(replaceAll);
                        title.setVisibility(8);
                        gridLayout.setVisibility(8);
                        scrollView.scrollTo(0, 0);
                        linearCategoryHead.setVisibility(0);
                        int i = 0;
                        while (i < featName.length) {
                            linearLayoutArr[i].setVisibility(i2 == i ? 0 : 8);
                            i++;
                        }
                        button.setEnabled(true);
                    }
                });

            TextView textView3 = icon_back;
            textView3.setOnClickListener(new OnClickListener() {
                    public  void onClick(View view) {
                        icon_back.setEnabled(false);
                        linearCategoryHead.setVisibility(8);
                        linearLayoutArr[Index].setVisibility(8);
                        scrollView.scrollTo(0, 0);
                        title.setVisibility(0);
                        categorymaintext.setText("");
                        gridLayout.setVisibility(0);
                        icon_back.setEnabled(true);
                    }
                });

            linearLayoutArr[i] = new LinearLayout(getContext);
            linearLayoutArr[i].setOrientation(1);
            linearLayoutArr[i].setVisibility(8);
            mTab[i] = linearLayoutArr[i];
            linearLayout3.addView(linearLayoutArr[i]);
            linearLayout5.addView(textView);
            linearLayout5.addView(textView2);
            linearLayout4.addView(linearLayout5);
            linearLayout4.addView(button);
            gridLayout.addView(linearLayout4);
        }

        linearLayout2.addView(gridLayout);
        linearLayout2.addView(linearLayout3);
        linearLayout.addView(linearLayout2);
    }

    private void Switch(LinearLayout linLayout, final int featNum, final String featName, boolean swiOn) {
        final Switch switchR = new Switch(getContext);
        final GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setSize(dp(45), dp(20));
        gradientDrawable.setCornerRadius(40.0f);

        final GradientDrawable gradientDrawable2 = new GradientDrawable();
        gradientDrawable2.setSize(dp(20), dp(20));
        gradientDrawable2.setShape(1);

        boolean isOn = Preferences.loadPrefBool(featName, featNum, swiOn);

        if (isOn) {
            gradientDrawable.setColor(ToggleTrackON);
            gradientDrawable.setStroke(dp(1), ToggleThumbON);
            gradientDrawable2.setColor(ToggleThumbON);
            gradientDrawable2.setStroke(dp(1), ToggleThumbON);
            switchR.setTrackDrawable(gradientDrawable);
            switchR.setThumbDrawable(gradientDrawable2);

        } else {
            gradientDrawable.setColor(ToggleTrackOFF);
            gradientDrawable.setStroke(dp(1), ToggleThumbON);
            gradientDrawable2.setColor(ToggleThumbOFF);
            gradientDrawable2.setStroke(dp(1), ToggleThumbON);
            switchR.setTrackDrawable(gradientDrawable);
            switchR.setThumbDrawable(gradientDrawable2);
        }
        switchR.setText(featName);
        switchR.setTextColor(TEXT_COLOR);
        switchR.setLayoutParams(new LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        switchR.setPadding(dp(4), dp(4), dp(4), dp(4));
        switchR.setChecked(Preferences.loadPrefBool(featName, featNum, swiOn));
        switchR.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton compoundButton, boolean bool) {
                    Preferences.changeFeatureBool(featName, featNum, bool);
                    if (bool) {
                        gradientDrawable.setColor(ToggleTrackON);
                        gradientDrawable.setStroke(dp(1), ToggleThumbON);

                        gradientDrawable2.setColor(ToggleThumbON);
                        gradientDrawable2.setStroke(dp(1), ToggleThumbON);
                        switchR.setTrackDrawable(gradientDrawable);
                        switchR.setThumbDrawable(gradientDrawable2);

                    } else {
                        gradientDrawable.setColor(ToggleTrackOFF);
                        gradientDrawable.setStroke(dp(1), ToggleThumbON);
                        gradientDrawable2.setColor(ToggleThumbOFF);
                        gradientDrawable2.setStroke(dp(1), ToggleThumbON);
                        switchR.setTrackDrawable(gradientDrawable);
                        switchR.setThumbDrawable(gradientDrawable2);
                    }
                    switch (featNum) {
                        case -1: //Save perferences
                            Preferences.with(switchR.getContext()).writeBoolean(-1, bool);
                            if (bool == false)
                                Preferences.with(switchR.getContext()).clear(); //Clear perferences if switched off
                            break;
                        case -3:
                            Preferences.isExpanded = bool;
                            scrollView.setLayoutParams(bool ? scrlLLExpanded : scrlLL);
                            break;
                        
                    }
                }
            });

        linLayout.addView(switchR);
    }

    private void SeekBar(LinearLayout linLayout, final int featNum, final String featName, final int min, int max) {
        int loadedProg = Preferences.loadPrefInt(featName, featNum);
        LinearLayout linearLayout = new LinearLayout(getContext);
        linearLayout.setPadding(dp(4), dp(2), 0, dp(2));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);

        final TextView textView = new TextView(getContext);
        if(Preferences.loadPref) {
            textView.setText(Html.fromHtml(featName + " -> <font color='" + "'>" + loadedProg + "(SAVED VALUE)"));
        }
        else {
            textView.setText(Html.fromHtml(featName + " -> <font color='" + "'>" +  "DEFAULT"));
        }
        textView.setTextColor(TEXT_COLOR);

        SeekBar seekBar = new SeekBar(getContext);
        seekBar.setPadding(dp(10), dp(4), dp(11), dp(4));
        seekBar.setMax(max);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            seekBar.setMin(min); //setMin for Oreo and above
        seekBar.setProgress((loadedProg == 0) ? min : loadedProg);
        seekBar.getThumb().setColorFilter(SeekBarColor, PorterDuff.Mode.SRC_ATOP);
        seekBar.getProgressDrawable().setColorFilter(SeekBarProgressColor, PorterDuff.Mode.SRC_ATOP);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                }

                public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                    //if progress is greater than minimum, don't go below. Else, set progress
                    if(i > min) {
                        seekBar.setProgress(i < min ? min : i);
                        Preferences.changeFeatureInt(featName, featNum, i < min ? min : i);
                        textView.setText(Html.fromHtml(featName + " -> <font color='" + "'>" + (i < min ? min : i) + "x"));
                    }
                    else {
                        seekBar.setProgress(i < min ? min : i);
                        Preferences.changeFeatureInt(featName, featNum, i < min ? min : i);
                        textView.setText(Html.fromHtml(featName + " -> <font color='" + "'>" + "DEFAULT"));
                    }
                    switch(featNum) {
                        case -4:
                            ICON_ALPHA = (i + 1.0f) / 10.f;
                            if(i == 9) {
                                textView.setText(featName + " -> " + "DEFAULT");
                            }
                            if(i == min) {
                                ICON_ALPHA = 1.f;
                            }
                            break;
                        case -5:
                            startimage.getLayoutParams().height = (i + 13) * 5;
                            startimage.getLayoutParams().width = (i + 13) * 5;
                            if(i == 13) {
                                textView.setText(featName + " -> " + "DEFAULT");
                            }
                            if(i == min) {
                                startimage.getLayoutParams().height = 150;
                                startimage.getLayoutParams().width = 150;
                            }
                            break;

                        case 208:
                            if(i == 5) {
                                textView.setText(featName + " -> " + "INFINITY");
                            }
                            break;
                    }
                }
            });
        linearLayout.addView(textView);
        linearLayout.addView(seekBar);

        linLayout.addView(linearLayout);
    }

    private void Button(LinearLayout linLayout, final int featNum, final String featName) {
        final Button button = new Button(getContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        layoutParams.setMargins(dp(7), dp(2), dp(7), dp(2));
        button.setLayoutParams(layoutParams);
        button.setTextColor(TEXT_COLOR_2);
        button.setAllCaps(false); //Disable caps to support html
        button.setText(Html.fromHtml(featName));
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(TEXT_COLOR);
        gradientDrawable.setCornerRadii(new float[]{dp(20),dp(20),0,0,dp(20),dp(20),0,0});
        button.setBackground(gradientDrawable);
        button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    switch (featNum) {

                        case -6:
                            mCollapsed.setVisibility(View.VISIBLE);
                            mCollapsed.setAlpha(0);
                            mExpanded.setVisibility(View.GONE);
                            //ShowToast("Icon hidden. Remember the hidden icon position");
                            break;
                        case -7:
                           // ShowToast("Menu killed");
                            rootFrame.removeView(mRootContainer);
                            mWindowManager.removeView(rootFrame);
                            break;
                        case -100:
                            stopChecking = true;
                            break;
                        case 22:
                            showPickShaderDialog();
                            return;
                    }
                    Preferences.changeFeatureInt(featName, featNum, 0);
                }
            });

        linLayout.addView(button);
    }

    private void ButtonLink(LinearLayout linLayout, final String featName, final String url) {
        final Button button = new Button(getContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        layoutParams.setMargins(dp(7), dp(2), dp(7), dp(2));
        button.setLayoutParams(layoutParams);
        button.setAllCaps(false); //Disable caps to support html
        button.setTextColor(TEXT_COLOR_2);
        button.setText(Html.fromHtml(featName));
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(TEXT_COLOR);
        gradientDrawable.setCornerRadii(new float[]{dp(20),dp(20),0,0,dp(20),dp(20),0,0});
        button.setBackground(gradientDrawable);
        button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse(url));
                    getContext.startActivity(intent);
                }
            });
        linLayout.addView(button);
    }
    private void ButtonOnOff(LinearLayout linLayout, final int featNum, String featName, boolean switchedOn) {
        final Button button = new Button(getContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        layoutParams.setMargins(7, 5, 7, 5);
        button.setLayoutParams(layoutParams);
        button.setTextColor(TEXT_COLOR_2);
        button.setAllCaps(false); //Disable caps to support html

        final String finalfeatName = featName.replace("OnOff_", "");
        boolean isOn = Preferences.loadPrefBool(featName, featNum, switchedOn);
        if (isOn) {
            button.setText(Html.fromHtml(finalfeatName + ": ON"));
            button.setBackgroundColor(BtnON);
            isOn = false;
        } else {
            button.setText(Html.fromHtml(finalfeatName + ": OFF"));
            button.setBackgroundColor(BtnOFF);
            isOn = true;
        }
        final boolean finalIsOn = isOn;
        button.setOnClickListener(new View.OnClickListener() {
            boolean isOn = finalIsOn;

            public void onClick(View v) {
                Preferences.changeFeatureBool(finalfeatName, featNum, isOn);
                //Log.d(TAG, finalfeatName + " " + featNum + " " + isActive2);
                if (isOn) {
                    button.setText(Html.fromHtml(finalfeatName + ": ON"));
                    button.setBackgroundColor(BtnON);
                    isOn = false;
                } else {
                    button.setText(Html.fromHtml(finalfeatName + ": OFF"));
                    button.setBackgroundColor(BtnOFF);
                    isOn = true;
                }
            }
        });
        linLayout.addView(button);
    }

    private void Spinner(LinearLayout linLayout,final int featNum, final String featName, final String featName2, final String list) {
        final String[] listArr = list.split(",");

        LinearLayout linearLayout2 = new LinearLayout(getContext);

        LayoutParams layoutParams = new LayoutParams(-1, -2);
        layoutParams.setMargins(dp(4), dp(4), dp(4), dp(4));
        linearLayout2.setOrientation(0);
        linearLayout2.setPadding(dp(4), dp(4), dp(4), dp(4));
        linearLayout2.setLayoutParams(layoutParams);

        GradientDrawable gradientDrawable = new GradientDrawable();
        // gradientDrawable.setColor(TEXT_COLOR_2);
        gradientDrawable.setStroke(dp(2), TEXT_COLOR);
        gradientDrawable.setCornerRadii(new float[]{dp(20),dp(20),dp(0),dp(0),dp(20),dp(20),dp(0),dp(0)});
        linearLayout2.setBackgroundDrawable(gradientDrawable);

        final int[] selectedPosition = new int[1];
        final TextView textView = new TextView(getContext);
        textView.setText(listArr[0]);
        textView.setTextColor(TEXT_COLOR);
        textView.setPadding(dp(2), dp(2), dp(2), dp(2));
        textView.setTextSize(15.0f);

        LayoutParams layoutParams2 = new LayoutParams(0, -2);
        layoutParams2.gravity = 8388627;
        layoutParams2.weight = 1.0f;
        textView.setLayoutParams(layoutParams2);

        TextView textView2 = new TextView(getContext);
        textView2.setTextColor(TEXT_COLOR);
        textView2.setText("arrow_drop_down");
        textView2.setTypeface(Typeface.createFromAsset(getContext.getAssets(), "mods.black"));
        textView2.setTextSize(35.0f);
        textView2.setGravity(17);
        textView2.setPadding(dp(2), dp(2), dp(2), dp(2));

        linearLayout2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(getContext);
                    } else {
                        builder = new AlertDialog.Builder(getContext);
                    }

                    builder.setTitle(featName2);
                    builder.setSingleChoiceItems(listArr, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                selectedPosition[0] = which;
                            }
                        });

                    builder.setPositiveButton("SELECT", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int i2 = selectedPosition[0];
                                if (i2 != -1) {
                                    textView.setText(listArr[i2]);
                                    Preferences.changeFeatureInt(list, featNum, i2);
                                }
                                dialog.dismiss();
                            }
                        });
                    builder.setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();

                            }
                        });

                    AlertDialog dialog = builder.create();
                    dialog.getWindow().setType(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

                    dialog.show();
                }
            });
        linearLayout2.addView(textView);
        linearLayout2.addView(textView2);
        linLayout.addView(linearLayout2);
	}

    private void InputNum(LinearLayout linLayout, final int featNum, final String featName, final int maxValue) {
        final EditTextNum edittextnum = new EditTextNum();

        LinearLayout linearLayout = new LinearLayout(getContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        layoutParams.setMargins(dp(4), dp(4), dp(4), dp(4));
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setPadding(dp(7), dp(4), dp(4), dp(4));
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setStroke(dp(2), TEXT_COLOR);
        gradientDrawable.setCornerRadii(new float[]{dp(20), dp(20), 0.0f, 0.0f, dp(20), dp(20), 0.0f, 0.0f});
        linearLayout.setBackground(gradientDrawable);
        linearLayout.setLayoutParams(layoutParams);


        LayoutParams lp = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lp.weight = 1.0f;

        LinearLayout linearLayout2 = new LinearLayout(getContext);
        linearLayout2.setLayoutParams(lp);
        linearLayout2.setOrientation(1);
        linearLayout2.setGravity(Gravity.CENTER);

        final TextView textView = new TextView(getContext);
        int num = Preferences.loadPrefInt(featName, featNum);
        edittextnum.setNum((num == 0) ? 0 : num);
        textView.setText(featName + "\n" + "-> " + ((num == 0) ? 0 : num));
        textView.setTextColor(TEXT_COLOR);
        textView.setTextSize(14.0F);
        textView.setGravity(3);
        textView.setPadding(5, 0, 0, 0);

        if(featNum == 108) {
            textView.setText(featName + "\n" + "-> " + "DEFAULT");
        }

        final Button button = new Button(getContext);
        button.setText("ENTER");
        GradientDrawable gradientDrawable2 = new GradientDrawable();
        gradientDrawable2.setColor(BTN_COLOR);
        gradientDrawable2.setCornerRadii(new float[]{dp(20), dp(20), 0, 0, dp(20), dp(20), 0, 0});
        gradientDrawable2.setStroke(dp(1), TEXT_COLOR);
        button.setBackground(gradientDrawable2);
        button.setTextColor(TEXT_COLOR_2);

        button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final AlertDialog alert = new AlertDialog.Builder(getContext, 2).create();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        Objects.requireNonNull(alert.getWindow()).setType(Build.VERSION.SDK_INT >= 26 ? 2038 : 2002);
                    }
                    alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            public void onCancel(DialogInterface dialog) {
                                InputMethodManager imm = (InputMethodManager) getContext.getSystemService(getContext.INPUT_METHOD_SERVICE);
                                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                            }
                        });

                    //LinearLayout
                    LinearLayout linearLayout1 = new LinearLayout(getContext);
                    linearLayout1.setPadding(dp(2),dp(2),dp(2),dp(2));
                    linearLayout1.setOrientation(LinearLayout.VERTICAL);
                    GradientDrawable gradientDrawable5 = new GradientDrawable();
                    gradientDrawable5.setColor(MENU_FEATURE_BG_COLOR);
                    gradientDrawable5.setCornerRadius(dp(5));
                    gradientDrawable5.setStroke(dp(2), TEXT_COLOR);
                    linearLayout1.setBackground(gradientDrawable5);
                    linearLayout1.setElevation(5.0F);

                    //TextView
                    final TextView titleText = new TextView(getContext);
                    titleText.setText(Html.fromHtml(new StringBuffer().append(new StringBuffer().append("<u>").append(featName).toString()).append("</u>").toString()));        
                    titleText.setGravity(17);
                    titleText.setTypeface(Typeface.DEFAULT_BOLD);
                    titleText.setTextColor(TEXT_COLOR);
                    titleText.setTextSize(22f);

                    LayoutParams lpl = new LayoutParams(MATCH_PARENT, WRAP_CONTENT);
                    lpl.weight = 1;

                    //Edit text
                    final EditText edittext = new EditText(getContext);
                    edittext.setLayoutParams(lpl);
                    edittext.setMaxLines(1);
                    edittext.setHint("Write Value");
                    edittext.setWidth(convertDipToPixels(300));
                    edittext.setTextColor(TEXT_COLOR);
                    edittext.setInputType(InputType.TYPE_CLASS_NUMBER);
                    edittext.setKeyListener(DigitsKeyListener.getInstance("0123456789-"));
                    InputFilter[] FilterArray = new InputFilter[1];
                    FilterArray[0] = new InputFilter.LengthFilter(10);
                    edittext.setFilters(FilterArray);
                    edittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                InputMethodManager imm = (InputMethodManager) getContext.getSystemService(getContext.INPUT_METHOD_SERVICE);
                                if (hasFocus) {
                                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                                } else {
                                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                                }
                            }
                        });
                    edittext.requestFocus();        

                    //Button
                    LayoutParams layoutParams = new LayoutParams(MATCH_PARENT, WRAP_CONTENT);
                    layoutParams.setMargins(dp(15), dp(10), dp(15), dp(10));

                    Button btndialog = new Button(getContext);
                    btndialog.setLayoutParams(layoutParams);
                    GradientDrawable gradientDrawable4 = new GradientDrawable();
                    gradientDrawable4.setColor(TEXT_COLOR_2);
                    gradientDrawable4.setCornerRadii(new float[]{dp(20), dp(20), 0, 0, dp(20), dp(20), 0, 0});
                    gradientDrawable4.setStroke(dp(2), TEXT_COLOR);
                    btndialog.setBackground(gradientDrawable4);
                    btndialog.setTextColor(TEXT_COLOR);
                    btndialog.setPadding(15,10,15,10);
                    btndialog.setText("SUBMIT");
                    btndialog.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                int num;
                                try {
                                    num = Integer.parseInt(TextUtils.isEmpty(edittext.getText().toString()) ? "0" : edittext.getText().toString());
                                    if (maxValue != 0 &&  num >= maxValue)
                                        num = maxValue;
                                } catch (NumberFormatException ex) {
                                    num = 2147483640;
                                }
                                edittextnum.setNum(num);
                                textView.setText(featName + "\n" + "-> " + num);
                                alert.dismiss();
                                Preferences.changeFeatureInt(featName, featNum, num);
                                edittext.setFocusable(false);
                            }
                        });
                    linearLayout1.addView(titleText);
                    linearLayout1.addView(edittext);
                    linearLayout1.addView(btndialog);
                    alert.setView(linearLayout1);
                    alert.show();
                }
            });

        linearLayout.addView(linearLayout2);
        linearLayout2.addView(textView);
        linearLayout.addView(button);

        linLayout.addView(linearLayout);
    }

    private void InputText(LinearLayout linLayout, final int featNum, final String featName) {
        final EditTextString edittextstring = new EditTextString();
        LinearLayout linearLayout = new LinearLayout(getContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        layoutParams.setMargins(dp(10), dp(5), dp(7), dp(5));
        linearLayout.setOrientation(0);
        linearLayout.setGravity(16);
        linearLayout.setPadding(dp(10), dp(5), dp(5), dp(5));
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setStroke(dp(2), TEXT_COLOR);
        gradientDrawable.setCornerRadii(new float[]{dp(20), dp(20), 0.0f, 0.0f, dp(20), dp(20), 0.0f, 0.0f});
        linearLayout.setBackground(gradientDrawable);
        linearLayout.setLayoutParams(layoutParams);


        LayoutParams lp = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lp.weight = 1.0f;

        LinearLayout linearLayout2 = new LinearLayout(getContext);
        linearLayout2.setLayoutParams(lp);
        linearLayout2.setOrientation(1);
        linearLayout2.setGravity(Gravity.CENTER);

        final TextView textView = new TextView(getContext);
        String string = Preferences.loadPrefString(featName, featNum);
        // Feat -10 = Target Library: show native default if no saved value yet
        if (featNum == -10 && (string == null || string.isEmpty())) {
            try { string = GetTargetLibrary(); } catch (Throwable t) { string = "libil2cpp.so"; }
        }
        edittextstring.setString((string == "") ? "" : string);
        textView.setText(featName + "\n" + "-> " + string );
        textView.setTextColor(TEXT_COLOR);
        textView.setTextSize(14.0F);
        textView.setGravity(3);
        textView.setSingleLine(true);
        textView.setPadding(5, 0, 0, 0);


        final Button button = new Button(getContext);
        button.setText("ENTER");
        GradientDrawable gradientDrawable2 = new GradientDrawable();
        gradientDrawable2.setColor(Color.WHITE);
        gradientDrawable2.setCornerRadii(new float[]{dp(20), dp(20), 0, 0, dp(20), dp(20), 0, 0});
        gradientDrawable2.setStroke(dp(1), TEXT_COLOR);
        button.setBackground(gradientDrawable2);
        button.setTextColor(TEXT_COLOR);

        button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final AlertDialog alert = new AlertDialog.Builder(getContext, 2).create();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        Objects.requireNonNull(alert.getWindow()).setType(Build.VERSION.SDK_INT >= 26 ? 2038 : 2002);
                    }
                    alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            public void onCancel(DialogInterface dialog) {
                                InputMethodManager imm = (InputMethodManager) getContext.getSystemService(getContext.INPUT_METHOD_SERVICE);
                                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                            }
                        });

                    //LinearLayout
                    LinearLayout linearLayout1 = new LinearLayout(getContext);
                    linearLayout1.setPadding(dp(2),dp(2),dp(2),dp(2));
                    linearLayout1.setOrientation(LinearLayout.VERTICAL);
                    GradientDrawable gradientDrawable5 = new GradientDrawable();
                    gradientDrawable5.setColor(MENU_FEATURE_BG_COLOR);
                    gradientDrawable5.setCornerRadius(dp(5));
                    gradientDrawable5.setStroke(dp(2), TEXT_COLOR);
                    linearLayout1.setBackground(gradientDrawable5);
                    linearLayout1.setElevation(5.0F);

                    //TextView
                    final TextView titleText = new TextView(getContext);
                    titleText.setText(Html.fromHtml(new StringBuffer().append(new StringBuffer().append("<u>").append(featName).toString()).append("</u>").toString()));        
                    titleText.setGravity(17);
                    titleText.setTypeface(Typeface.DEFAULT_BOLD);
                    titleText.setTextColor(TEXT_COLOR);
                    titleText.setTextSize(22f);

                    LayoutParams lpl = new LayoutParams(MATCH_PARENT, WRAP_CONTENT);
                    lpl.weight = 1;

                    //Edit text
                    final EditText edittext = new EditText(getContext);
                    edittext.setLayoutParams(lpl);
                    edittext.setMaxLines(1);
                    edittext.setHint("Write Text");
                    edittext.setWidth(convertDipToPixels(300));
                    edittext.setTextColor(TEXT_COLOR);
                    edittext.setText(edittextstring.getString());
                    edittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                InputMethodManager imm = (InputMethodManager) getContext.getSystemService(getContext.INPUT_METHOD_SERVICE);
                                if (hasFocus) {
                                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                                } else {
                                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                                }
                            }
                        });
                    edittext.requestFocus();

                    //Button
                    LayoutParams layoutParams = new LayoutParams(MATCH_PARENT, WRAP_CONTENT);
                    layoutParams.setMargins(dp(15), dp(10), dp(15), dp(10));

                    Button btndialog = new Button(getContext);
                    btndialog.setLayoutParams(layoutParams);
                    GradientDrawable gradientDrawable4 = new GradientDrawable();
                    gradientDrawable4.setColor(Color.WHITE);
                    gradientDrawable4.setCornerRadii(new float[]{dp(20), dp(20), 0, 0, dp(20), dp(20), 0, 0});
                    gradientDrawable4.setStroke(dp(2), TEXT_COLOR);
                    btndialog.setBackground(gradientDrawable4);
                    btndialog.setTextColor(TEXT_COLOR);
                    btndialog.setPadding(15,10,15,10);
                    btndialog.setText("SUBMIT");
                    btndialog.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String str = edittext.getText().toString();
                                edittextstring.setString(edittext.getText().toString());
                                textView.setText(featName + "\n" + "-> " + str);
                                alert.dismiss();
                                Preferences.changeFeatureString(featName, featNum, str);
                                edittext.setFocusable(false);
                            }
                        });
                    linearLayout1.addView(titleText);
                    linearLayout1.addView(edittext);
                    linearLayout1.addView(btndialog);
                    alert.setView(linearLayout1);
                    alert.show();
                }
            });

        linearLayout.addView(linearLayout2);
        linearLayout2.addView(textView);
        linearLayout.addView(button);

        linLayout.addView(linearLayout);
    }

    private void CheckBox(LinearLayout linLayout, final int featNum, final String featName, boolean switchedOn) {
        final CheckBox checkBox = new CheckBox(getContext);
        checkBox.setText(featName);
        checkBox.setTextColor(TEXT_COLOR);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            checkBox.setButtonTintList(ColorStateList.valueOf(CheckBoxColor));
        checkBox.setChecked(Preferences.loadPrefBool(featName, featNum, switchedOn));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkBox.isChecked()) {
                    Preferences.changeFeatureBool(featName, featNum, isChecked);
                } else {
                    Preferences.changeFeatureBool(featName, featNum, isChecked);
                }
            }
        });
        linLayout.addView(checkBox);
    }

    private void RadioButton(LinearLayout linLayout, final int featNum, String featName, final String list) {
        //Credit: LoraZalora
        final List<String> lists = new LinkedList<>(Arrays.asList(list.split(",")));

        final TextView textView = new TextView(getContext);
        textView.setText(featName + ":");
        textView.setTextColor(TEXT_COLOR_2);

        final RadioGroup radioGroup = new RadioGroup(getContext);
        radioGroup.setPadding(10, 5, 10, 5);
        radioGroup.setOrientation(LinearLayout.VERTICAL);
        radioGroup.addView(textView);

        for (int i = 0; i < lists.size(); i++) {
            final RadioButton Radioo = new RadioButton(getContext);
            final String finalfeatName = featName, radioName = lists.get(i);
            View.OnClickListener first_radio_listener = new View.OnClickListener() {
                public void onClick(View v) {
                    textView.setText(Html.fromHtml(finalfeatName + ": <font color='" +  "'>" + radioName));
                    Preferences.changeFeatureInt(finalfeatName, featNum, radioGroup.indexOfChild(Radioo));
                }
            };
            System.out.println(lists.get(i));
            Radioo.setText(lists.get(i));
            Radioo.setTextColor(Color.LTGRAY);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                Radioo.setButtonTintList(ColorStateList.valueOf(RadioColor));
            Radioo.setOnClickListener(first_radio_listener);
            radioGroup.addView(Radioo);
        }

        int index = Preferences.loadPrefInt(featName, featNum);
        if (index > 0) { //Preventing it to get an index less than 1. below 1 = null = crash
            textView.setText(Html.fromHtml(featName + ": <font color='"  + "'>" + lists.get(index - 1)));
            ((RadioButton) radioGroup.getChildAt(index)).setChecked(true);
        }
        linLayout.addView(radioGroup);
    }

    private void Collapse(LinearLayout linLayout, final String text, final boolean expanded) {
        LinearLayout.LayoutParams layoutParamsLL = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        layoutParamsLL.setMargins(0, 5, 0, 0);

        LinearLayout collapse = new LinearLayout(getContext);
        collapse.setLayoutParams(layoutParamsLL);
        collapse.setVerticalGravity(16);
        collapse.setOrientation(LinearLayout.VERTICAL);

        final LinearLayout collapseSub = new LinearLayout(getContext);
        collapseSub.setVerticalGravity(16);
        collapseSub.setPadding(0, 5, 0, 5);
        collapseSub.setOrientation(LinearLayout.VERTICAL);
        collapseSub.setBackgroundColor(Color.parseColor("#222D38"));
        collapseSub.setVisibility(View.GONE);
        mCollapse = collapseSub;

        final TextView textView = new TextView(getContext);
        textView.setBackgroundColor(CategoryBG);
        textView.setText("▽ " + text + " ▽");
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(TEXT_COLOR_2);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setPadding(0, 20, 0, 20);

        if (expanded) {
            collapseSub.setVisibility(View.VISIBLE);
            textView.setText("△ " + text + " △");
        }

        textView.setOnClickListener(new View.OnClickListener() {
            boolean isChecked = expanded;

            @Override
            public void onClick(View v) {

                boolean z = !isChecked;
                isChecked = z;
                if (z) {
                    collapseSub.setVisibility(View.VISIBLE);
                    textView.setText("△ " + text + " △");
                    return;
                }
                collapseSub.setVisibility(View.GONE);
                textView.setText("▽ " + text + " ▽");
            }
        });
        collapse.addView(textView);
        collapse.addView(collapseSub);
        linLayout.addView(collapse);
    }

    private void Category(LinearLayout linLayout, boolean z, String text) {
        LayoutParams layoutParams = new LayoutParams(-1, -2);
        layoutParams.setMargins(dp(5), dp(2), dp(5), 0);
        TextView textView = new TextView(getContext);
        textView.setText(Html.fromHtml(text));
        textView.setGravity(17);
        textView.setTextColor(this.TEXT_COLOR_2);
        textView.setLayoutParams(layoutParams);
        textView.setTypeface(Typeface.SERIF, 1);
        textView.setPadding(0, dp(2), 0, dp(2));
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(TEXT_COLOR);
        gradientDrawable.setCornerRadius((float) dp(10));
        textView.setBackground(gradientDrawable);
        if(!z) {
            categorymaintext = textView;
        }
        linLayout.addView(textView);
    }

    private void TextView(LinearLayout linLayout, String text) {
        TextView textView = new TextView(getContext);
        textView.setText(Html.fromHtml(text));
        textView.setTextColor(TEXT_COLOR);
        textView.setPadding(10, 5, 10, 5);
        linLayout.addView(textView);
    }

    private void WebTextView(LinearLayout linLayout, String text) {
        WebView wView = new WebView(getContext);
        wView.loadData(text, "text/html", "utf-8");
        wView.setBackgroundColor(0x00000000); //Transparent
        wView.setPadding(0, 5, 0, 5);
        wView.getSettings().setAppCacheEnabled(false);
        linLayout.addView(wView);
    }
    
    public class EditTextString {
        private String text;

        public void setString(String s) {
            text = s;
        }

        public String getString() {
            return text;
        }
    }

    public class EditTextNum {
        private int val;

        public void setNum(int i) {
            val = i;
        }

        public int getNum() {
            return val;
        }
    }

    private boolean isViewCollapsed() {
        return rootFrame == null || mCollapsed.getVisibility() == View.VISIBLE;
    }

    //For our image a little converter
    private int convertDipToPixels(int i) {
        return (int) ((((float) i) * getContext.getResources().getDisplayMetrics().density) + 0.5f);
    }

    private int dp(int i) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) i, getContext.getResources().getDisplayMetrics());
    }

    public void setVisibility(int view) {
        if (rootFrame != null) {
            rootFrame.setVisibility(view);
        }
    }

    public void onDestroy() {
        if (rootFrame != null) {
            mWindowManager.removeView(rootFrame);
        }
        hideShaderInspector();
        if (libStatusHandler != null && libStatusRunnable != null) {
            libStatusHandler.removeCallbacks(libStatusRunnable);
        }
        libStatusHandler = null;
        libStatusRunnable = null;
        libStatusView = null;
    }

    // ====================================================================
    // Library status row (settings tab)
    // ====================================================================
    private void addLibraryStatusView(LinearLayout parent) {
        libStatusView = new TextView(getContext);
        libStatusView.setTextColor(TEXT_COLOR);
        libStatusView.setTextSize(13f);
        libStatusView.setPadding(dp(10), dp(4), dp(10), dp(6));
        libStatusView.setText(Html.fromHtml("Library Status: <i>checking...</i>"));
        parent.addView(libStatusView);

        if (libStatusHandler != null && libStatusRunnable != null) {
            libStatusHandler.removeCallbacks(libStatusRunnable);
        }
        libStatusHandler = new Handler();
        libStatusRunnable = new Runnable() {
            @Override
            public void run() {
                if (libStatusView == null) return;
                String lib;
                boolean loaded;
                try {
                    lib = GetTargetLibrary();
                    loaded = IsTargetLibraryLoaded();
                } catch (Throwable t) {
                    lib = "?";
                    loaded = false;
                }
                String color = loaded ? "#1B5E20" : "#B71C1C";
                String state = loaded ? "Loaded" : "Not loaded";
                libStatusView.setText(Html.fromHtml(
                        "Library Status: <b>" + lib + "</b> &mdash; " +
                        "<font color='" + color + "'><b>" + state + "</b></font>"));
                libStatusHandler.postDelayed(this, 1000);
            }
        };
        libStatusHandler.post(libStatusRunnable);
    }

    // ====================================================================
    // Shader Picker dialog (RadioGroup + search)
    // ====================================================================
    private void showPickShaderDialog() {
        final String[] captured = GetShaderList();
        final List<String> all = new ArrayList<String>();
        all.add("OFF");
        if (captured != null) {
            for (String s : captured) all.add(s);
        }

        final AlertDialog alert = new AlertDialog.Builder(getContext).create();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(alert.getWindow()).setType(Build.VERSION.SDK_INT >= 26 ? 2038 : 2002);
        }

        LinearLayout root = new LinearLayout(getContext);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(10), dp(10), dp(10), dp(10));
        GradientDrawable rootBg = new GradientDrawable();
        rootBg.setColor(MENU_FEATURE_BG_COLOR);
        rootBg.setCornerRadius(dp(10));
        rootBg.setStroke(dp(2), TEXT_COLOR);
        root.setBackground(rootBg);

        LinearLayout titleBar = new LinearLayout(getContext);
        titleBar.setOrientation(LinearLayout.HORIZONTAL);
        titleBar.setGravity(Gravity.CENTER_VERTICAL);
        titleBar.setPadding(dp(10), dp(8), dp(10), dp(8));
        GradientDrawable titleBg = new GradientDrawable();
        titleBg.setColor(MENU_BG_COLOR);
        titleBg.setCornerRadii(new float[]{dp(8), dp(8), dp(8), dp(8), 0, 0, 0, 0});
        titleBar.setBackground(titleBg);
        LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        titleLp.setMargins(0, 0, 0, dp(8));
        titleBar.setLayoutParams(titleLp);

        TextView title = new TextView(getContext);
        title.setText("Pick Shader [" + (all.size() - 1) + "]");
        title.setTextColor(TEXT_COLOR_2);
        title.setTextSize(16f);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        titleBar.addView(title);
        root.addView(titleBar);

        final EditText search = new EditText(getContext);
        search.setHint("Search...");
        search.setHintTextColor(Color.parseColor("#999999"));
        search.setTextColor(TEXT_COLOR);
        search.setSingleLine(true);
        search.setTextSize(13f);
        search.setPadding(dp(10), dp(4), dp(10), dp(4));
        GradientDrawable searchBg = new GradientDrawable();
        searchBg.setColor(MENU_FEATURE_BG_COLOR);
        searchBg.setStroke(dp(2), TEXT_COLOR);
        searchBg.setCornerRadii(new float[]{dp(20), dp(20), 0, 0, dp(20), dp(20), 0, 0});
        search.setBackground(searchBg);
        LinearLayout.LayoutParams searchLp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        searchLp.setMargins(0, 0, 0, dp(8));
        search.setLayoutParams(searchLp);
        root.addView(search);

        ScrollView scroll = new ScrollView(getContext);
        scroll.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, dp(320)));

        final RadioGroup group = new RadioGroup(getContext);
        group.setOrientation(LinearLayout.VERTICAL);

        final String current = GetCurrentShader();
        final int[] selectedIdx = new int[]{-1};

        final Runnable rebuild = new Runnable() {
            @Override
            public void run() {
                group.removeAllViews();
                String q = shaderSearchQuery.toLowerCase();
                for (int i = 0; i < all.size(); i++) {
                    final String name = all.get(i);
                    if (!q.isEmpty() && !name.toLowerCase().contains(q)) continue;

                    RadioButton rb = new RadioButton(getContext);
                    rb.setText(name);
                    rb.setTextColor(TEXT_COLOR);
                    rb.setTextSize(14f);
                    rb.setPadding(dp(8), dp(6), dp(8), dp(6));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        rb.setButtonTintList(ColorStateList.valueOf(TEXT_COLOR));
                    }
                    if (name.equals(current) || (i == 0 && (current == null || current.equals("Off") || current.isEmpty()))) {
                        rb.setChecked(true);
                        selectedIdx[0] = i;
                    }
                    final int idx = i;
                    rb.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectedIdx[0] = idx;
                        }
                    });
                    group.addView(rb);
                }
            }
        };

        shaderSearchQuery = "";
        rebuild.run();

        search.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                shaderSearchQuery = s.toString();
                rebuild.run();
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        scroll.addView(group);
        root.addView(scroll);
        
        LinearLayout btnRow = new LinearLayout(getContext);
        btnRow.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams btnRowLp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        btnRowLp.setMargins(0, dp(8), 0, 0);
        btnRow.setLayoutParams(btnRowLp);

        LinearLayout.LayoutParams pillLp = new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f);
        pillLp.setMargins(dp(4), 0, dp(4), 0);

        Button close = new Button(getContext);
        close.setText("Close");
        close.setTextColor(TEXT_COLOR_2);
        close.setAllCaps(false);
        GradientDrawable gdClose = new GradientDrawable();
        gdClose.setColor(TEXT_COLOR);
        gdClose.setCornerRadii(new float[]{dp(20), dp(20), 0, 0, dp(20), dp(20), 0, 0});
        close.setBackground(gdClose);
        close.setLayoutParams(pillLp);
        close.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { alert.dismiss(); }
        });

        Button select = new Button(getContext);
        select.setText("Select");
        select.setTextColor(TEXT_COLOR_2);
        select.setAllCaps(false);
        GradientDrawable gdSelect = new GradientDrawable();
        gdSelect.setColor(TEXT_COLOR);
        gdSelect.setCornerRadii(new float[]{dp(20), dp(20), 0, 0, dp(20), dp(20), 0, 0});
        select.setBackground(gdSelect);
        select.setLayoutParams(pillLp);
        select.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (selectedIdx[0] >= 0 && selectedIdx[0] < all.size()) {
                    String pick = all.get(selectedIdx[0]);
                    SetCurrentShader(pick.equals("OFF") ? "Off" : pick);
                }
                alert.dismiss();
            }
        });
        btnRow.addView(close);
        btnRow.addView(select);
        root.addView(btnRow);

        alert.setView(root);
        alert.show();
    }

    // ====================================================================
    // Floating draggable Shader Inspector (independent WindowManager view)
    // ====================================================================
    @SuppressLint("WrongConstant")
    private void showShaderInspector() {
        if (shaderInspectorShown) {
            return;
        }

        LinearLayout container = new LinearLayout(getContext);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(dp(10), dp(10), dp(10), dp(10));
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(MENU_FEATURE_BG_COLOR);
        bg.setCornerRadius(dp(10));
        bg.setStroke(dp(2), TEXT_COLOR);
        container.setBackground(bg);
        container.setLayoutParams(new LinearLayout.LayoutParams(dp(280), WRAP_CONTENT));

        LinearLayout header = new LinearLayout(getContext);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(8), dp(6), dp(8), dp(6));
        GradientDrawable headerBg = new GradientDrawable();
        headerBg.setColor(MENU_BG_COLOR);
        headerBg.setCornerRadii(new float[]{dp(8), dp(8), dp(8), dp(8), 0, 0, 0, 0});
        header.setBackground(headerBg);
        LinearLayout.LayoutParams headerLp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        headerLp.setMargins(0, 0, 0, dp(8));
        header.setLayoutParams(headerLp);

        shaderInspectorTitle = new TextView(getContext);
        shaderInspectorTitle.setText("Shaders [0]");
        shaderInspectorTitle.setTextColor(TEXT_COLOR_2);
        shaderInspectorTitle.setTypeface(Typeface.DEFAULT_BOLD);
        shaderInspectorTitle.setTextSize(16f);
        LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f);
        shaderInspectorTitle.setLayoutParams(titleLp);
        header.addView(shaderInspectorTitle);

        TextView copyAll = new TextView(getContext);
        copyAll.setText("\u2398 Copy");
        copyAll.setTextColor(TEXT_COLOR_2);
        copyAll.setPadding(dp(8), dp(2), dp(8), dp(2));
        copyAll.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                String[] list = GetShaderList();
                StringBuilder sb = new StringBuilder();
                if (list != null) for (String s : list) sb.append(s).append('\n');
                ClipboardManager cm = (ClipboardManager) getContext.getSystemService("clipboard");
                cm.setPrimaryClip(ClipData.newPlainText("shaders", sb.toString()));
                Toast.makeText(getContext, "Copied " + (list == null ? 0 : list.length) + " shaders", Toast.LENGTH_SHORT).show();
            }
        });
        header.addView(copyAll);

        TextView closeX = new TextView(getContext);
        closeX.setText("  \u2715  ");
        closeX.setTextColor(TEXT_COLOR_2);
        closeX.setTypeface(Typeface.DEFAULT_BOLD);
        closeX.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { hideShaderInspector(); }
        });
        header.addView(closeX);

        container.addView(header);
        
        shaderSearchBox = new EditText(getContext);
        shaderSearchBox.setHint("Search...");
        shaderSearchBox.setHintTextColor(Color.parseColor("#999999"));
        shaderSearchBox.setTextColor(TEXT_COLOR);
        shaderSearchBox.setSingleLine(true);
        shaderSearchBox.setTextSize(13f);
        shaderSearchBox.setPadding(dp(10), dp(4), dp(10), dp(4));
        GradientDrawable sBg = new GradientDrawable();
        sBg.setColor(MENU_FEATURE_BG_COLOR);
        sBg.setStroke(dp(2), TEXT_COLOR);
        sBg.setCornerRadii(new float[]{dp(20), dp(20), 0, 0, dp(20), dp(20), 0, 0});
        shaderSearchBox.setBackground(sBg);
        LinearLayout.LayoutParams sLp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        sLp.setMargins(0, 0, 0, dp(6));
        shaderSearchBox.setLayoutParams(sLp);
        shaderSearchBox.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                shaderSearchQuery = s.toString();
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });
        container.addView(shaderSearchBox);

        ScrollView scroll = new ScrollView(getContext);
        scroll.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, dp(280)));
        shaderListContainer = new LinearLayout(getContext);
        shaderListContainer.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(shaderListContainer);
        container.addView(scroll);

        LinearLayout footer = new LinearLayout(getContext);
        footer.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams fLp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        fLp.setMargins(0, dp(8), 0, 0);
        footer.setLayoutParams(fLp);

        Button clearBtn = new Button(getContext);
        clearBtn.setText("Clear");
        clearBtn.setTextColor(TEXT_COLOR_2);
        clearBtn.setAllCaps(false);
        GradientDrawable gdClear = new GradientDrawable();
        gdClear.setColor(TEXT_COLOR);
        gdClear.setCornerRadii(new float[]{dp(20), dp(20), 0, 0, dp(20), dp(20), 0, 0});
        clearBtn.setBackground(gdClear);
        LinearLayout.LayoutParams bLp = new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f);
        bLp.setMargins(dp(4), 0, dp(4), 0);
        clearBtn.setLayoutParams(bLp);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                ClearShaders();
            }
        });

        Button exportBtn = new Button(getContext);
        exportBtn.setText("Export");
        exportBtn.setTextColor(TEXT_COLOR_2);
        exportBtn.setAllCaps(false);
        GradientDrawable gdExport = new GradientDrawable();
        gdExport.setColor(TEXT_COLOR);
        gdExport.setCornerRadii(new float[]{dp(20), dp(20), 0, 0, dp(20), dp(20), 0, 0});
        exportBtn.setBackground(gdExport);
        exportBtn.setLayoutParams(bLp);
        exportBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                exportShaders();
            }
        });

        footer.addView(clearBtn);
        footer.addView(exportBtn);
        container.addView(footer);

        int type = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? 2038 : 2002;
        shaderInspectorParams = new WindowManager.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, type, 8, -3);
        shaderInspectorParams.gravity = Gravity.TOP | Gravity.START;
        shaderInspectorParams.x = dp(20);
        shaderInspectorParams.y = dp(120);

        container.setOnTouchListener(shaderInspectorTouchListener());

        shaderInspectorRoot = container;
        try {
            mWindowManager.addView(shaderInspectorRoot, shaderInspectorParams);
            shaderInspectorShown = true;
        } catch (Exception e) {
            Log.e(TAG, "Failed to add shader inspector", e);
            return;
        }

        shaderRefreshHandler = new Handler();
        shaderRefreshRunnable = new Runnable() {
            @Override
            public void run() {
                if (!shaderInspectorShown) return;
                refreshShaderList();
                shaderRefreshHandler.postDelayed(this, 500);
            }
        };
        shaderRefreshHandler.post(shaderRefreshRunnable);
    }

    private void hideShaderInspector() {
        if (!shaderInspectorShown) return;
        try {
            if (shaderInspectorRoot != null) mWindowManager.removeView(shaderInspectorRoot);
        } catch (Exception ignored) {}
        shaderInspectorShown = false;
        shaderInspectorRoot = null;
        shaderListContainer = null;
        shaderInspectorTitle = null;
        if (shaderRefreshHandler != null && shaderRefreshRunnable != null) {
            shaderRefreshHandler.removeCallbacks(shaderRefreshRunnable);
        }
        shaderRefreshHandler = null;
        shaderRefreshRunnable = null;
    }

    private void refreshShaderList() {
        if (shaderListContainer == null) return;
        String[] list = GetShaderList();
        if (list == null) list = new String[0];

        String current = GetCurrentShader();
        if (current == null) current = "Off";
        String query = shaderSearchQuery == null ? "" : shaderSearchQuery.toLowerCase();

        shaderInspectorTitle.setText("Shaders [" + list.length + "]");
        shaderListContainer.removeAllViews();

        for (int i = 0; i < list.length; i++) {
            final String name = list[i];
            if (!query.isEmpty() && !name.toLowerCase().contains(query)) continue;

            final TextView row = new TextView(getContext);
            row.setText(name);
            row.setTextSize(13f);
            row.setTypeface(Typeface.MONOSPACE);
            row.setPadding(dp(10), dp(6), dp(10), dp(6));
            boolean isCurrent = name.equals(current);
            row.setTextColor(isCurrent ? TEXT_COLOR_2 : TEXT_COLOR);
            if (isCurrent) {
                GradientDrawable rowBg = new GradientDrawable();
                rowBg.setColor(TEXT_COLOR);
                rowBg.setCornerRadius(dp(4));
                row.setBackground(rowBg);
            }
            LinearLayout.LayoutParams rowLp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            rowLp.setMargins(0, dp(1), 0, dp(1));
            row.setLayoutParams(rowLp);

            row.setOnLongClickListener(new View.OnLongClickListener() {
                @Override public boolean onLongClick(View v) {
                    ClipboardManager cm = (ClipboardManager) getContext.getSystemService("clipboard");
                    cm.setPrimaryClip(ClipData.newPlainText("shader", name));
                    Toast.makeText(getContext, "Copied: " + name, Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
            shaderListContainer.addView(row);
        }
    }

    private void exportShaders() {
        try {
            String[] list = GetShaderList();
            File dir = getContext.getExternalFilesDir(null);
            if (dir == null) {
                Toast.makeText(getContext, "Export failed: no external dir", Toast.LENGTH_SHORT).show();
                return;
            }
            File out = new File(dir, "shaders.txt");
            FileWriter w = new FileWriter(out, false);
            if (list != null) {
                for (String s : list) {
                    w.write(s);
                    w.write('\n');
                }
            }
            w.close();
            Toast.makeText(getContext, "Exported to " + out.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e(TAG, "exportShaders", e);
            Toast.makeText(getContext, "Export failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private View.OnTouchListener shaderInspectorTouchListener() {
        return new View.OnTouchListener() {
            float touchX, touchY;
            int initX, initY;
            long downTime;

            @Override
            public boolean onTouch(View v, MotionEvent e) {
                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initX = shaderInspectorParams.x;
                        initY = shaderInspectorParams.y;
                        touchX = e.getRawX();
                        touchY = e.getRawY();
                        downTime = System.currentTimeMillis();
                        return false;
                    case MotionEvent.ACTION_MOVE:
                        shaderInspectorParams.x = initX + (int) (e.getRawX() - touchX);
                        shaderInspectorParams.y = initY + (int) (e.getRawY() - touchY);
                        try {
                            mWindowManager.updateViewLayout(shaderInspectorRoot, shaderInspectorParams);
                        } catch (Exception ignored) {}
                        return false;
                    case MotionEvent.ACTION_UP:
                        return false;
                }
                return false;
            }
        };
    }
}
