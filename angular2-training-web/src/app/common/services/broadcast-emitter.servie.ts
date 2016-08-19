/**
 * Created by zemo on 19/08/16.
 */
import {Injectable} from '@angular/core';
import {BroadcasterService} from "./broadcaster.service";

@Injectable()
export class BroadcastEmmitterService {

  constructor(private broadcasterService: BroadcasterService){}

  /**
   * Emit or fire any message identified by key
   * @param data - data to emit
   * @param key - identifier for consumer
   */
  emit(data: any, key: string): void {
    this.broadcasterService.broadcast(key, data);
  }
}
