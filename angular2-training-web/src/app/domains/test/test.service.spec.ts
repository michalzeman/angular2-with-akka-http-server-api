import {TestService} from "./test.service";
import {
  BaseRequestOptions, Http, Response, ResponseOptions, Headers,
  ResponseType
} from "@angular/http";
import {
  TestBed,
  inject
} from "@angular/core/testing";
import {MockBackend} from "@angular/http/testing";
import {Test} from "./test";
import {DelegateService} from "../../common/services/delegate.service";
import {BroadcastEmmitterService} from "../../common/services/broadcast-emitter.servie";
import {BroadcasterService} from "../../common/services/broadcaster.service";

describe('TestCrudService', () => {

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        BaseRequestOptions,
        MockBackend,
        {
          provide: Http,
          useFactory: function (backend, defaultOptions) {
            return new Http(backend, defaultOptions);
          },
          deps: [MockBackend, BaseRequestOptions]
        },

        TestService,
        DelegateService,
        BroadcastEmmitterService,
        BroadcasterService
      ]
    })
  });

  it('TestCrudService first test', inject([TestService, MockBackend], (testCrudService, mockBackend) => {
    let response = new Test();
    response.id = 1;

    let responseOptions = new ResponseOptions({body: response});
    mockBackend.connections.subscribe(
      c => c.mockRespond(new Response(responseOptions)));

    testCrudService.get(1).subscribe(data => {
      console.log('Response: ', data);
      expect(data.id).toBe(1);
    });
  }));
});
