import {Injectable} from "@angular/core";
import {BaseEntityServiceImpl} from "../../common/services/entity/base-entity.service";
import {Item} from "./Item";
import {DelegateService} from "../../common/services/delegate.service";
import {Http} from "@angular/http";
import {Observable} from "rxjs";

@Injectable()
export class ItemService extends BaseEntityServiceImpl<Item> {

  private _items = [
    {
      id: 1,
      name: 'Keyboard',
      description: 'PC keyboard'
    }, {
      id: 2,
      name: 'SD',
      description: 'SD card'
    }, {
      id: 5,
      name: '4GBRam',
      description: 'PC memory'
    }, {
      id: 13,
      name: 'SSD',
      description: 'SSD 2,5"'
    },
  ];

  constructor(delegateService: DelegateService, http: Http) {
    super(delegateService, http, '/items');
  }

  getAll(): Observable<Item[]> {
    return Observable.create(observer => {
        observer.next(this._items);
        observer.complete();
      }
    )
  }

  get(id: number): Observable<Item> {
    return Observable.create(observer => {
      let item:Item = this._items.find(item => item.id === id);
      observer.next((item)? item:undefined);
      observer.complete();
    });
  }

}
