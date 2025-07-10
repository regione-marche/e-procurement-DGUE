import { Injectable } from '@angular/core';
import { AppCostants } from '../utils/dgue-constants';


@Injectable({providedIn: 'root'})
export class StoreService{
    
    addElement(storeKey: string,jsonContent :any, encode? :Boolean){        
        localStorage.setItem(storeKey, JSON.stringify(jsonContent));                
    }

    clearStore(){
        localStorage.clear();
    }

    viewElement(storeKey : string,encode? :Boolean){       
        return  JSON.parse(localStorage.getItem(storeKey));                
    }

}