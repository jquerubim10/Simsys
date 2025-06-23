import { Injectable } from "@angular/core";
import { Subject, Observable } from "rxjs";

@Injectable({
    providedIn: 'root'
  })
export class DynamicSaveClick {
    private subject = new Subject<any>();

    sendClickEvent(id){
      this.subject.next(id);
    }
  
    getClickEvent(): Observable<any> {
       return this.subject.asObservable();
    }

    destroy() {
      this.subject = new Subject<any>();
    }
}