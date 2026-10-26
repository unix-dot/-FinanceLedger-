package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.adapter.FinanceGridAdapter;
import com.example.myapplication.adapter.RecordAdapter;
import com.example.myapplication.model.FinanceRecord;
import com.example.myapplication.model.GridItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 金融财务明细与服务矩阵界面 (LedgerDetailActivity)
 *
 * 核心必做满足：
 * 1. 至少2个Activity：作为第二个页面，实现Intent页面间传值接收与渲染
 * 2. 在所有页面静态写死本人姓名、学号，程序运行时不可编辑修改
 * 3. 选用备选控件：ListView、GridView、ImageButton
 */
public class LedgerDetailActivity extends AppCompatActivity {

    // 基础控件定义
    private TextView tvStudentSignature;
    private TextView tvIntentType;
    private TextView tvIntentAmount;
    private TextView tvIntentTime;
    private TextView tvIntentAccountInfo;
    private TextView tvIntentFlags;
    private TextView tvRecordCount;

    private Button btnBackMain;
    private Button btnClearList;

    // 备选控件1：ImageButton (顶部返回按钮)
    private ImageButton ibDetailBack;

    // 备选控件5：ListView (展示历史账单明细流水)
    private ListView lvRecords;
    private RecordAdapter recordAdapter;
    private List<FinanceRecord> recordList;

    // 备选控件6：GridView (展示金融综合服务矩阵)
    private GridView gvFinanceTools;
    private FinanceGridAdapter gridAdapter;
    private List<GridItem> gridItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ledger_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detail_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. 初始化视图控件
        initViews();

        // 2. 静态写死本人身份信息（不可编辑修改）
        initSignature();

        // 3. 接收并解析来自 MainActivity 的 Intent 传值数据
        handleIntentData();

        // 4. 初始化并填充 GridView (备选控件6)
        setupGridView();

        // 5. 初始化并填充 ListView (备选控件5)
        setupListView();

        // 6. 绑定点击交互事件
        initListeners();
    }

    /**
     * 绑定布局控件
     */
    private void initViews() {
        tvStudentSignature = findViewById(R.id.tv_detail_student_signature);
        tvIntentType = findViewById(R.id.tv_intent_type);
        tvIntentAmount = findViewById(R.id.tv_intent_amount);
        tvIntentTime = findViewById(R.id.tv_intent_time);
        tvIntentAccountInfo = findViewById(R.id.tv_intent_account_info);
        tvIntentFlags = findViewById(R.id.tv_intent_flags);
        tvRecordCount = findViewById(R.id.tv_record_count);

        btnBackMain = findViewById(R.id.btn_back_main);
        btnClearList = findViewById(R.id.btn_clear_list);
        ibDetailBack = findViewById(R.id.ib_detail_back);

        lvRecords = findViewById(R.id.lv_records);
        gvFinanceTools = findViewById(R.id.gv_finance_tools);
    }

    /**
     * 核心必做2：在所有页面静态写死本人姓名、学号，程序运行时不可编辑修改
     */
    private void initSignature() {
        String signText = "【金融系统应用开发】 班级：" + MainActivity.STUDENT_CLASS 
                + " | 学号：" + MainActivity.STUDENT_ID 
                + " | 姓名：" + MainActivity.STUDENT_NAME;
        tvStudentSignature.setText(signText);
        tvStudentSignature.setFocusable(false);
        tvStudentSignature.setClickable(false);
    }

    /**
     * 核心必做1：接收来自 MainActivity 的 Intent 传值并展示在顶部卡片
     */
    private void handleIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            String type = intent.getStringExtra("EXTRA_TYPE");
            double amount = intent.getDoubleExtra("EXTRA_AMOUNT", 0.0);
            String account = intent.getStringExtra("EXTRA_ACCOUNT");
            String remark = intent.getStringExtra("EXTRA_REMARK");
            boolean isBudget = intent.getBooleanExtra("EXTRA_IS_BUDGET", false);
            boolean isUrgent = intent.getBooleanExtra("EXTRA_IS_URGENT", false);
            boolean isRemind = intent.getBooleanExtra("EXTRA_IS_REMIND", false);
            String time = intent.getStringExtra("EXTRA_TIME");

            if (type == null) type = "支出";
            if (account == null) account = "招商银行储蓄卡";
            if (remark == null) remark = "常规交易";
            if (time == null) time = "2026-10-12 17:00";

            // 渲染到顶部卡片
            tvIntentType.setText(type);
            if ("支出".equals(type)) {
                tvIntentType.setTextColor(Color.parseColor("#D32F2F"));
                tvIntentAmount.setText(String.format(Locale.CHINA, "-¥ %.2f", amount));
                tvIntentAmount.setTextColor(Color.parseColor("#D32F2F"));
            } else {
                tvIntentType.setTextColor(Color.parseColor("#2E7D32"));
                tvIntentAmount.setText(String.format(Locale.CHINA, "+¥ %.2f", amount));
                tvIntentAmount.setTextColor(Color.parseColor("#2E7D32"));
            }

            tvIntentTime.setText(time);
            tvIntentAccountInfo.setText(String.format("结算渠道：%s | 摘要：%s", account, remark));

            StringBuilder flags = new StringBuilder("金融特征：");
            if (isBudget) flags.append("[计入预算] ");
            if (isUrgent) flags.append("[大额关注] ");
            if (isRemind) flags.append("[到期提醒] ");
            if (!isBudget && !isUrgent && !isRemind) flags.append("[普通交易]");
            tvIntentFlags.setText(flags.toString());
        }
    }

    /**
     * 备选控件6：配置金融综合服务矩阵 (GridView)
     */
    private void setupGridView() {
        gridItemList = new ArrayList<>();
        gridItemList.add(new GridItem("资产分析", R.drawable.ic_finance, "查看资产负债分布与净值走势"));
        gridItemList.add(new GridItem("预算预警", R.drawable.ic_chart, "设定当月消费警戒线"));
        gridItemList.add(new GridItem("理财估算", R.drawable.ic_finance, "银行定存与基金收益复利测算"));
        gridItemList.add(new GridItem("流水对账", R.drawable.ic_chart, "导出电子对账单与Excel数据"));
        gridItemList.add(new GridItem("汇率换算", R.drawable.ic_finance, "实时多币种离岸汇率查询"));
        gridItemList.add(new GridItem("系统说明", R.drawable.ic_chart, "关于本金融APP及开发信息"));

        gridAdapter = new FinanceGridAdapter(this, gridItemList);
        gvFinanceTools.setAdapter(gridAdapter);

        // GridView 点击交互监听
        gvFinanceTools.setOnItemClickListener((parent, view, position, id) -> {
            GridItem clicked = gridItemList.get(position);
            showGridItemDialog(clicked);
        });
    }

    /**
     * 备选控件5：配置历史账单明细流水 (ListView)
     */
    private void setupListView() {
        recordList = new ArrayList<>();

        // 将当前 Intent 传进来的最新一笔加入到列表第1位
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("EXTRA_AMOUNT")) {
            String type = intent.getStringExtra("EXTRA_TYPE");
            double amount = intent.getDoubleExtra("EXTRA_AMOUNT", 0.0);
            String account = intent.getStringExtra("EXTRA_ACCOUNT");
            String remark = intent.getStringExtra("EXTRA_REMARK");
            boolean isBudget = intent.getBooleanExtra("EXTRA_IS_BUDGET", false);
            boolean isUrgent = intent.getBooleanExtra("EXTRA_IS_URGENT", false);
            boolean isRemind = intent.getBooleanExtra("EXTRA_IS_REMIND", false);
            String time = intent.getStringExtra("EXTRA_TIME");

            recordList.add(new FinanceRecord(
                    type != null ? type : "支出",
                    amount,
                    account != null ? account : "招商银行",
                    remark != null ? remark : "最新记账",
                    time != null ? time : "刚刚",
                    isBudget, isUrgent, isRemind
            ));
        }

        // 添加预置的金融模拟流水数据
        recordList.add(new FinanceRecord("收入", 6500.00, "工商银行储蓄卡 (尾号8812)", "月度工资及绩效奖金", "2026-10-10 10:00", false, true, false));
        recordList.add(new FinanceRecord("支出", 128.00, "微信支付 (零钱通)", "超市生活物资采购", "2026-10-11 18:45", true, false, false));
        recordList.add(new FinanceRecord("收入", 260.50, "支付宝 (余额宝)", "货币基金理财收益到账", "2026-10-12 08:30", false, false, true));
        recordList.add(new FinanceRecord("支出", 45.00, "建设银行信用卡 (信用账户)", "交通出行打车费", "2026-10-12 14:15", true, false, false));

        recordAdapter = new RecordAdapter(this, recordList);
        lvRecords.setAdapter(recordAdapter);
        updateRecordCount();

        // ListView 列表项点击交互：弹出金融电子对账单详情
        lvRecords.setOnItemClickListener((parent, view, position, id) -> {
            FinanceRecord record = recordList.get(position);
            showRecordDetailDialog(record);
        });
    }

    /**
     * 更新列表数量提示
     */
    private void updateRecordCount() {
        tvRecordCount.setText(String.format(Locale.CHINA, "共 %d 笔", recordList.size()));
    }

    /**
     * 控件点击交互监听
     */
    private void initListeners() {
        // 备选控件1 (ImageButton) 返回
        ibDetailBack.setOnClickListener(v -> finish());

        // 基础控件 (Button) 返回主页
        btnBackMain.setOnClickListener(v -> finish());

        // 基础控件 (Button) 重置列表
        btnClearList.setOnClickListener(v -> {
            recordList.clear();
            recordList.add(new FinanceRecord("收入", 8000.00, "工商银行储蓄卡", "初始账户结存注资", "2026-10-01 09:00", false, false, false));
            recordAdapter.notifyDataSetChanged();
            updateRecordCount();
            Toast.makeText(LedgerDetailActivity.this, "已重置为初始基础流水", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * 弹出 GridView 功能模块详情提示弹窗
     */
    private void showGridItemDialog(GridItem item) {
        new AlertDialog.Builder(this)
                .setTitle("【金融服务】" + item.getName())
                .setMessage("功能说明：" + item.getDescription() + "\n\n状态：金融核心服务运行正常。")
                .setPositiveButton("我知道了", null)
                .show();
    }

    /**
     * 弹出 ListView 单笔金融交易电子对账单弹窗
     */
    private void showRecordDetailDialog(FinanceRecord record) {
        String details = "【交易类型】" + record.getType() + "\n"
                + "【交易金额】¥ " + String.format(Locale.CHINA, "%.2f", record.getAmount()) + "\n"
                + "【结算账户】" + record.getAccount() + "\n"
                + "【交易摘要】" + record.getRemark() + "\n"
                + "【交易时间】" + record.getTime() + "\n"
                + "【预算管理】" + (record.isBudget() ? "已纳入当月硬性预算限额" : "未纳入预算") + "\n"
                + "【风险级别】" + (record.isUrgent() ? "重要/大额资金流动" : "常规低风险") + "\n"
                + "【系统状态】金融交易已成功入账";

        new AlertDialog.Builder(this)
                .setTitle("金融电子对账单详情")
                .setMessage(details)
                .setPositiveButton("确定", null)
                .show();
    }
}
