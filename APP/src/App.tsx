import React, { useEffect, useState } from "react"
import './App.css'
import axios from 'axios';
import type { Busdoko } from "./type/busdoko.type";

function App() {

  const [data, setData] = useState<Busdoko | null>(null);
  


  // API取得
  useEffect(() => {
    const getData = async () => {
      const url = "http://localhost:8080/get_busdoko";
      try {
        const response = await fetch(url);
        if (!response.ok) {
          // response.ok はステータスが200番台かどうかを true/false で示す
          console.error(`HTTP error! Status: ${response.status}`);
          throw new Error(`Failed to fetch data: ${response.statusText}`);
        }

        const jsondata = await response.json();

        setData(jsondata);
      } catch (error) {
        console.error(error);
        console.error("fetchできません")
        return null;
      }
    };

    getData();
    console.error(data?.delay);
  })


  return (
    <>
      <div className="singboard">
        <div className="header">
          六間坂上　浜松駅方面
        </div>
        <div className="next">
          <span className="orange">次発　　　</span>
          <span className="time">{data?.departure_time}</span>
        </div>
        <div className="keitou">{data?.bus_number}</div>
      </div>
      <div className="predict">
        <div className="touchaku">
          <span></span>
          <span className="delay">遅れ約 {data?.delay} 分</span>
        </div>
      </div>
    </>
  )
}

export default App