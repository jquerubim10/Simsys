import { Injectable } from "@angular/core";
import { Subject, Observable } from "rxjs";

@Injectable({
    providedIn: 'root'
  })
export class DynamicListClick {
    private subject = new Subject<any>();

    sendClickEvent(){
      this.subject.next(null);
  
    }
  
    getClickEvent(): Observable<any> {
       return this.subject.asObservable();
    }
}