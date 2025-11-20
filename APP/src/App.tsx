import { useEffect, useState } from "react"
import './App.css'
import type { Busdoko } from "./type/busdoko.type";
import type { JSX } from "react"

function App() {

  const [next, setNext] = useState<Busdoko | null>(null);
  const [nextnext, setNextNext] = useState<Busdoko | null>(null);
  const [count, setCount] = useState<number>(1);
  const [visible, setVisible] = useState<boolean>(true);


  // API取得
  useEffect(() => {
    const getData = async () => {
      const url = "http://localhost:8080/get_busdoko";
      console.error("hoge");
      try {
        const response = await fetch(url);
        if (!response.ok) {
          // response.ok はステータスが200番台かどうかを true/false で示す
          console.error(`HTTP error! Status: ${response.status}`);
          throw new Error(`Failed to fetch next: ${response.statusText}`);
        }

        const jsonnext = await response.json();

        if (jsonnext.departure_time != null)
          setNext(jsonnext);
        else
          console.error("データの取得に失敗")
      } catch (error) {
        console.error(error);
        console.error("fetchできません")
        return null;
      }
    };

    // 2. 1分（60000ミリ秒）ごとにfetchDataを実行するタイマーを設定
    getData();
    setCount(count + 1);
    const intervalId = setInterval(() => {
      getData();
      setCount(count + 1);
      console.error(count);
    }, 60 * 1000); // 60秒 * 1000ミリ秒 = 1分

    const intervalIdId = setInterval(() => {
      setVisible((prevvisible) => !prevvisible);
    }, 1 * 1000); // 60秒 * 1000ミリ秒 = 1分

    // 3. コンポーネントが非表示（アンマウント）になる際にタイマーを停止する
    //    これがないと、メモリリークの原因になります。
    return () => {
      clearInterval(intervalId);
      clearInterval(intervalIdId);
    };
  }, []);

  if (next == null) {
    return (
      <>
        <div className="singboard">
          <div className="header">
            六間坂上　浜松駅方面
          </div>
        </div>
        <div className="standby">
          ★　調　整　中　★
        </div>
      </>
    )
  }

  const next_departure: JSX.Element = (
    <div className="bus_item">
      <div className="next">
        <span className="orange">次発</span>
        {/* 5個前より手前なら点滅させる */}
        {visible || Number(next.previous) > 5 ? <span className="keitou">{next?.bus_number}</span> : <span className="toumei"></span>}
        <span className="time">{next?.departure_time}</span>
      </div>
      <div className="delay">{Number(next.delay) != 0 && `遅れ約 ${next?.delay} 分`}</div>
      <div className="touchaku">ただいま {next?.previous} 個前を走行中...</div>
      <div className="delay">{Number(next.delay) == 0 && <br></br>}</div>
    </div>
  )

  const next_next_depature: JSX.Element = (
    <div className="bus_item">
      <div className="next">
        <span className="orange">次次発</span>
        {visible ? <span className="keitou">{nextnext?.bus_number}</span> : <span className="toumei">{nextnext?.bus_number}</span>}
        <span className="time">{nextnext?.departure_time}</span>
      </div>
      <div className="predict">
        <div className="touchaku">
          <span className="delay">遅れ約 {nextnext?.delay} 分</span>
          <div>ただいま {nextnext?.previous} 個前を走行中...</div>
        </div>
      </div>
    </div>
  )


  return (
    <>
      <div className="signboard">
        <div className="header">
          六間坂上　浜松駅方面
        </div>
        {next_departure}
        {next_next_depature}
      </div>
    </>
  )
}

export default App