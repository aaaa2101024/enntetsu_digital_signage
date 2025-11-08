import React from "react"
import './App.css'
import axios from 'axios';
import type { Busdoko } from "./type/busdoko.type";

function App() {



  // API取得
  async function getData() {
    const url = "http://localhost:8080/get_busdoko";

    try {
      console.log(1);
      const response = await fetch(url);
      console.log(1);
      
      if (!response.ok) {
        // response.ok はステータスが200番台かどうかを true/false で示す
        console.error(`HTTP error! Status: ${response.status}`);
        throw new Error(`Failed to fetch data: ${response.statusText}`);
      }
      
      console.log(1);
      const data = await response.json() as Busdoko;

      console.log(data.bus_number);
    } catch (error) {
      console.error(error);
      return null;
    }
  }

  (async () => {
    const data = await getData();
  })();


  return (
    <>
      <div className="singboard">
        <div className="header">
          浜松駅方面
        </div>
      </div>
    </>
  )
}

export default App