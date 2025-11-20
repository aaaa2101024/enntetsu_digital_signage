import { useEffect, useState } from "react"
import './App.css'
import type { Busdoko } from "./type/busdoko.type";
import type { JSX } from "react"

function App() {

  const [data, setData] = useState<Busdoko | null>(null);
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
          throw new Error(`Failed to fetch data: ${response.statusText}`);
        }

        const jsondata = await response.json();

        if (jsondata.departure_time != null)
          setData(jsondata);
        else
          console.error("データの取得に失敗")
      } catch (error) {
        console.error(error);
        console.error("fetchできません")
        return null;
      }
    };

    // getData();
    // setCount(count + 1);
    // console.error(count);
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

  if (data == null) {
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
        {visible ? <span className="keitou">{data?.bus_number}　</span> : <span className="toumei">{data?.bus_number}　</span>}
        <span className="time">{data?.departure_time}</span>
      </div>
      <div className="predict">
        <div className="touchaku">
          <span className="delay">遅れ約 {data?.delay} 分</span>
          <div>ただいま {data?.previous} 個前を走行中...</div>
        </div>
      </div>
    </div>
  )

  const next_next_depature: JSX.Element = (
    <div className="bus_item">
      <div className="next">
        <span className="orange">次次発</span>
        {visible ? <span className="keitou">{data?.bus_number}　</span> : <span className="toumei">{data?.bus_number}　</span>}
        <span className="time">{data?.departure_time}</span>
      </div>
      <div className="predict">
        <div className="touchaku">
          <span className="delay">遅れ約 {data?.delay} 分</span>
          <div>ただいま {data?.previous} 個前を走行中...</div>
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