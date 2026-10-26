package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 金融个人记账与资产管理 - 主界面 (MainActivity)
 *
 * 核心必做满足：
 * 1. 基础控件 TextView、EditText、Button 具备正常交互，Intent页面跳转与传值
 * 2. 静态写死本人姓名、学号、班级（程序运行时不可编辑修改）
 * 3. 选用4种备选控件：ImageButton、RadioGroup、Spinner、CheckBox
 */
public class MainActivity extends AppCompatActivity {

    // 静态常驻身份信息常量（计科241 | 学号 | 姓名）
    public static final String STUDENT_CLASS = "计科241";
    public static final String STUDENT_ID = "2406100107";
    public static final String STUDENT_NAME = "方语晗";

    // 基础控件定义
    private TextView tvStudentSignature;
    private TextView tvTotalBalance;
    private TextView tvTodayExpense;
    private TextView tvTodayIncome;
    private EditText etAmount;
    private EditText etRemark;
    private Button btnSaveRecord;
    private Button btnGoDetail;

    // 备选控件1：ImageButton (用于清空表单快捷操作)
    private ImageButton ibReset;

    // 备选控件2：RadioGroup / RadioButton (用于选择交易类型：支出 / 收入)
    private RadioGroup rgTradeType;
    private RadioButton rbExpense;
    private RadioButton rbIncome;

    // 备选控件3：Spinner (用于选择结算金融账户)
    private Spinner spAccount;

    // 备选控件4：CheckBox (用于勾选金融特征：预算、重要大额、提醒)
    private CheckBox cbBudget;
    private CheckBox cbUrgent;
    private CheckBox cbRemind;

    // 金融资产统计数据状态
    private double currentBalance = 28650.00;
    private double currentTodayExpense = 0.00;
    private double currentTodayIncome = 0.00;

    // 最近一笔保存的记账数据（用于Intent页面传值）
    private String lastType = "支出";
    private double lastAmount = 68.50;
    private String lastAccount = "招商银行理财卡 (尾号6608)";
    private String lastRemark = "商务午餐与咖啡";
    private boolean lastIsBudget = true;
    private boolean lastIsUrgent = false;
    private boolean lastIsRemind = false;
    private String lastTime = "2026-10-12 12:30";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. 初始化所有视图控件
        initViews();

        // 2. 绑定静态写死的本人信息（程序运行不可修改）
        initSignature();

        // 3. 设置控件事件监听器与交互逻辑
        initListeners();
    }

    /**
     * 初始化视图绑定
     */
    private void initViews() {
        tvStudentSignature = findViewById(R.id.tv_student_signature);
        tvTotalBalance = findViewById(R.id.tv_total_balance);
        tvTodayExpense = findViewById(R.id.tv_today_expense);
        tvTodayIncome = findViewById(R.id.tv_today_income);

        etAmount = findViewById(R.id.et_amount);
        etRemark = findViewById(R.id.et_remark);

        btnSaveRecord = findViewById(R.id.btn_save_record);
        btnGoDetail = findViewById(R.id.btn_go_detail);

        ibReset = findViewById(R.id.ib_reset);

        rgTradeType = findViewById(R.id.rg_trade_type);
        rbExpense = findViewById(R.id.rb_expense);
        rbIncome = findViewById(R.id.rb_income);

        spAccount = findViewById(R.id.sp_account);

        cbBudget = findViewById(R.id.cb_budget);
        cbUrgent = findViewById(R.id.cb_urgent);
        cbRemind = findViewById(R.id.cb_remind);

        // 初始化显示资产看板
        updateDashboardView();
    }

    /**
     * 核心必做2：在所有页面静态写死本人姓名、学号，程序运行时不可编辑修改
     */
    private void initSignature() {
        String signText = "【金融系统应用开发】 班级：" + STUDENT_CLASS 
                + " | 学号：" + STUDENT_ID 
                + " | 姓名：" + STUDENT_NAME;
        tvStudentSignature.setText(signText);
        tvStudentSignature.setFocusable(false);
        tvStudentSignature.setClickable(false);
    }

    /**
     * 初始化各个控件的事件交互监听
     */
    private void initListeners() {
        // 备选控件1 (ImageButton) 交互：一键清空表单
        ibReset.setOnClickListener(v -> resetForm());

        // 备选控件2 (RadioGroup) 交互：切换交易性质
        rgTradeType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_expense) {
                etAmount.setHint("请输入支出金额 (元)");
                Toast.makeText(MainActivity.this, "已切换为：支出模式", Toast.LENGTH_SHORT).show();
            } else if (checkedId == R.id.rb_income) {
                etAmount.setHint("请输入收入金额 (元)");
                Toast.makeText(MainActivity.this, "已切换为：理财/收入模式", Toast.LENGTH_SHORT).show();
            }
        });

        // 备选控件3 (Spinner) 交互：账户选择反馈
        spAccount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String account = parent.getItemAtPosition(position).toString();
                // 用户切换账户时的交互体验
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // 基础控件 (Button) 交互1：确认记账保存
        btnSaveRecord.setOnClickListener(v -> handleSaveRecord());

        // 核心必做1：基础控件 (Button) 交互2：页面跳转并使用 Intent 传值
        btnGoDetail.setOnClickListener(v -> navigateToDetailActivity());
    }

    /**
     * 处理保存记账逻辑（含输入校验、计算、UI更新）
     */
    private void handleSaveRecord() {
        String amountStr = etAmount.getText().toString().trim();
        String remarkStr = etRemark.getText().toString().trim();

        // 输入有效性校验
        if (TextUtils.isEmpty(amountStr)) {
            etAmount.setError("请输入有效金额！");
            etAmount.requestFocus();
            Toast.makeText(this, "请输入交易金额", Toast.LENGTH_SHORT).show();
            return;
        }

        double inputAmount;
        try {
            inputAmount = Double.parseDouble(amountStr);
            if (inputAmount <= 0) {
                etAmount.setError("金额必须大于0元！");
                etAmount.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etAmount.setError("金额格式不正确！");
            return;
        }

        // 获取收支类型
        boolean isExpense = (rgTradeType.getCheckedRadioButtonId() == R.id.rb_expense);
        String selectedType = isExpense ? "支出" : "收入";
        String selectedAccount = spAccount.getSelectedItem().toString();

        if (TextUtils.isEmpty(remarkStr)) {
            remarkStr = isExpense ? "日常生活消费" : "理财收益入账";
        }

        // 金融逻辑计算
        if (isExpense) {
            currentBalance -= inputAmount;
            currentTodayExpense += inputAmount;
        } else {
            currentBalance += inputAmount;
            currentTodayIncome += inputAmount;
        }

        // 格式化当前时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String currentTime = sdf.format(new Date());

        // 缓存为最近一条交易数据（供Intent传递）
        lastType = selectedType;
        lastAmount = inputAmount;
        lastAccount = selectedAccount;
        lastRemark = remarkStr;
        lastIsBudget = cbBudget.isChecked();
        lastIsUrgent = cbUrgent.isChecked();
        lastIsRemind = cbRemind.isChecked();
        lastTime = currentTime;

        // 更新顶部资产统计看板
        updateDashboardView();

        // 隐藏软键盘
        hideKeyboard();

        // 弹出友好的操作提示
        String toastMsg = String.format(Locale.CHINA, "记账成功！%s ¥%.2f (%s)", 
                selectedType, inputAmount, selectedAccount);
        Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();

        // 清空输入框以便下次快速输入
        etAmount.setText("");
        etRemark.setText("");
    }

    /**
     * 核心必做1：页面跳转与 Intent 传值
     */
    private void navigateToDetailActivity() {
        // 创建显式 Intent，从 MainActivity 跳转至 LedgerDetailActivity
        Intent intent = new Intent(MainActivity.this, LedgerDetailActivity.class);

        // 通过 Intent 传递交易详细数据
        intent.putExtra("EXTRA_TYPE", lastType);
        intent.putExtra("EXTRA_AMOUNT", lastAmount);
        intent.putExtra("EXTRA_ACCOUNT", lastAccount);
        intent.putExtra("EXTRA_REMARK", lastRemark);
        intent.putExtra("EXTRA_IS_BUDGET", lastIsBudget);
        intent.putExtra("EXTRA_IS_URGENT", lastIsUrgent);
        intent.putExtra("EXTRA_IS_REMIND", lastIsRemind);
        intent.putExtra("EXTRA_TIME", lastTime);
        intent.putExtra("EXTRA_TOTAL_BALANCE", currentBalance);

        // 启动目标 Activity
        startActivity(intent);
    }

    /**
     * 重置清空表单
     */
    private void resetForm() {
        etAmount.setText("");
        etRemark.setText("");
        rbExpense.setChecked(true);
        spAccount.setSelection(0);
        cbBudget.setChecked(true);
        cbUrgent.setChecked(false);
        cbRemind.setChecked(false);
        Toast.makeText(this, "表单已重置清空", Toast.LENGTH_SHORT).show();
    }

    /**
     * 刷新顶部资产看板文本
     */
    private void updateDashboardView() {
        tvTotalBalance.setText(String.format(Locale.CHINA, "¥ %,.2f", currentBalance));
        tvTodayExpense.setText(String.format(Locale.CHINA, "¥ %,.2f", currentTodayExpense));
        tvTodayIncome.setText(String.format(Locale.CHINA, "¥ %,.2f", currentTodayIncome));
    }

    /**
     * 收起软键盘
     */
    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}