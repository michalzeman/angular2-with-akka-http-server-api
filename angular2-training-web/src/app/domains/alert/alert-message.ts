/**
 * Created by zemo on 19/08/16.
 */
export class AlertMessage {

  constructor(public message:string, public type:string) {}

}

export const ALERT_TYPE_SUCCESS = "alert-success";
export const ALERT_TYPE_WARNING = "alert-warning";
export const ALERT_TYPE_INFO = "alert-info";
export const ALERT_TYPE_DANGER = "alert-danger";
