/**
 * Created by zemo on 19/08/16.
 */
import {Injectable} from '@angular/core';
import {Subject} from 'rxjs/Subject';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/filter';
import 'rxjs/add/operator/map';

interface BroadcastEvent {
  key: any;
  data?: any;
}

@Injectable()
export class BroadcasterService {
  private eventBus: Subject<BroadcastEvent>;

  constructor() {
    this.eventBus = new Subject<BroadcastEvent>();
  }

  broadcast(key: any, data?: any) {
    this.eventBus.next({key, data});
  }

  on<T>(key: any): Observable<T> {
    return this.eventBus.asObservable()
      .filter(event => event.key === key)
      .map(event => <T>event.data);
  }
}
