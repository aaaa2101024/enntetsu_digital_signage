# テスト


## スクレイピングで得る情報とその方法
### 得る情報
- 六間坂上から最も近い, 浜松駅を終点とするバスが
    - 何個前のバス停を走行中であるか
    - 何分に到着するか
    - 現在何分遅延しているか

### 方法
前提として, [六間坂上から浜松駅へのバスどこ!?](https://transfer-cloud.navitime.biz/entetsu/approachings?departure-busstop=00460589&arrival-busstop=00460001)を使用」
- 何個前のバスか
    - バスどこ!?リストの一番上を取得
- 何分に到着するか
    - 系統番号などを保存しておき, 「発着時刻表」を参照
    - その系統番号であり, 現在時刻以降の中で直近のものを参照
- 何分遅延しているか
    - 系統番号を保存しておき, 「途中バス停」を参照
    - 一番上にある時刻表を参照
    - 系統番号を含む時刻表を参照
    - 「すべて選択・解除」をクリック=>その系統番号をクリック=>現在時刻以降の中で, 最も直近のものを参照


### 想定されるJSON
```json
{
    "bus": "[40]聖隷三方原　尾張町　浜松駅", // 系統番号
    "time": "22:21", // 本来の出発時刻
    "delay": 3, // 遅延時間
    "pres": 23 // 何個前のバス停か
}
```

### class-idなど(メイン画面)
```
mx-4 mt-4 flex justify-between : 系統
flex items-center justify-center rounded border border-button bg-white px-2 text-button hover:no-underline w-auto h-10 text-base grow : 発着時刻表, 途中バス停
mx-1 text-2xl : 何個前のバス停か
```

### 発着時刻表
```
text-[22px] font-bold : 発車時刻と到着時刻(2nなので注意)
font-bold : 系統番号(完全一致ならおｋ)
```

### 途中のバス停
```
flex h-full min-w-[2.5rem] items-center break-all text-xs text-link : 途中のバス停(一番上をとればおｋ)
.flex.h-full.min-w-\\[2\\.5rem\\].items-center.break-all.text-xs.text-link
```

### 系統・時刻表・のりば一覧
```
w-[676px] space-y-4 px-6 py-4 : ul-liで方面を拾う
```
ul-liだからforの方が速そう

### 時刻表
```
my-2 ml-0.5 mr-4 h-5 w-5 cursor-pointer accent-link : チェックボックス
cursor-pointer print:ml-0.5 : 系統を取得
mt-6 w-full table-fixed border-collapse border border-dark-line : 時刻表テーブル
py-1 text-lg : 時刻表テーブルの時刻の部分

```


## 得た情報の活用
予想浜松駅到着時刻から, 乗り換えられる最も近い在来線を表示


### 備忘録
曜日が祝日対応していない=>本当にやりたくない
マジックナンバーだらけ=>本当にやりたくない