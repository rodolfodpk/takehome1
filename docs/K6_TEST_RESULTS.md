# k6 Test Results

This file contains the latest results from k6 performance tests. Results are automatically updated after each test run.

## Warm-up Test - 2025-11-13 21:19:52


         /\      Grafana   /‾‾/  
    /\  /  \     |\  __   /  /   
   /  \/    \    | |/ /  /   ‾‾\ 
  /          \   |   (  |  (‾)  |
 / __________ \  |_|\_\  \_____/ 

     execution: local
        script: k6/scripts/warmup-test.js
        output: -

     scenarios: (100.00%) 1 scenario, 2 max VUs, 40s max duration (incl. graceful stop):
              * default: Up to 2 looping VUs for 10s over 2 stages (gracefulRampDown: 30s, gracefulStop: 30s)


running (01.0s), 1/2 VUs, 265 complete and 0 interrupted iterations
default   [  10% ] 1/2 VUs  01.0s/10.0s

running (02.0s), 1/2 VUs, 642 complete and 0 interrupted iterations
default   [  20% ] 1/2 VUs  02.0s/10.0s

running (03.0s), 1/2 VUs, 958 complete and 0 interrupted iterations
default   [  30% ] 1/2 VUs  03.0s/10.0s

running (04.0s), 1/2 VUs, 1154 complete and 0 interrupted iterations
default   [  40% ] 1/2 VUs  04.0s/10.0s

running (05.0s), 1/2 VUs, 1372 complete and 0 interrupted iterations
default   [  50% ] 1/2 VUs  05.0s/10.0s

running (06.0s), 2/2 VUs, 2004 complete and 0 interrupted iterations
default   [  60% ] 2/2 VUs  06.0s/10.0s

running (07.0s), 2/2 VUs, 2598 complete and 0 interrupted iterations
default   [  70% ] 2/2 VUs  07.0s/10.0s

running (08.0s), 2/2 VUs, 3198 complete and 0 interrupted iterations
default   [  80% ] 2/2 VUs  08.0s/10.0s

running (09.0s), 2/2 VUs, 3686 complete and 0 interrupted iterations
default   [  90% ] 2/2 VUs  09.0s/10.0s

running (10.0s), 2/2 VUs, 4315 complete and 0 interrupted iterations
default   [ 100% ] 2/2 VUs  10.0s/10.0s


  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(95)<500' p(95)=5.26ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.00%


  █ TOTAL RESULTS 

    checks_total.......................: 25926   2592.201319/s
    checks_succeeded...................: 100.00% 25926 out of 25926
    checks_failed......................: 0.00%   0 out of 25926

    ✓ ingest event status is 201
    ✓ ingest event has eventId in response
    ✓ ingest event has status in response
    ✓ ingest event has processedAt in response
    ✓ warmup: response time < 500ms
    ✓ warmup: response has body

    HTTP
    http_req_duration.......................................................: avg=3.3ms  min=1.75ms med=2.87ms max=76.97ms p(90)=4.53ms p(95)=5.26ms
      { expected_response:true }............................................: avg=3.3ms  min=1.75ms med=2.87ms max=76.97ms p(90)=4.53ms p(95)=5.26ms
    http_req_failed.........................................................: 0.00%  0 out of 4321
    http_reqs...............................................................: 4321   432.033553/s

    EXECUTION
    iteration_duration......................................................: avg=3.46ms min=1.83ms med=3ms    max=92.22ms p(90)=4.74ms p(95)=5.54ms
    iterations..............................................................: 4321   432.033553/s
    vus.....................................................................: 2      min=1         max=2
    vus_max.................................................................: 2      min=2         max=2

    NETWORK
    data_received...........................................................: 786 kB 79 kB/s
    data_sent...............................................................: 1.7 MB 174 kB/s




running (10.0s), 0/2 VUs, 4321 complete and 0 interrupted iterations
default ✓ [ 100% ] 0/2 VUs  10s


## Smoke Test - 2025-11-13 21:21:17


         /\      Grafana   /‾‾/  
    /\  /  \     |\  __   /  /   
   /  \/    \    | |/ /  /   ‾‾\ 
  /          \   |   (  |  (‾)  |
 / __________ \  |_|\_\  \_____/ 

     execution: local
        script: k6/scripts/smoke-test.js
        output: -

     scenarios: (100.00%) 1 scenario, 10 max VUs, 1m30s max duration (incl. graceful stop):
              * default: Up to 10 looping VUs for 1m0s over 2 stages (gracefulRampDown: 30s, gracefulStop: 30s)


running (0m01.0s), 01/10 VUs, 280 complete and 0 interrupted iterations
default   [   2% ] 01/10 VUs  0m01.0s/1m00.0s

running (0m02.0s), 01/10 VUs, 574 complete and 0 interrupted iterations
default   [   3% ] 01/10 VUs  0m02.0s/1m00.0s

running (0m03.0s), 01/10 VUs, 805 complete and 0 interrupted iterations
default   [   5% ] 01/10 VUs  0m03.0s/1m00.0s

running (0m04.0s), 02/10 VUs, 1157 complete and 0 interrupted iterations
default   [   7% ] 02/10 VUs  0m04.0s/1m00.0s

running (0m05.0s), 02/10 VUs, 1712 complete and 0 interrupted iterations
default   [   8% ] 02/10 VUs  0m05.0s/1m00.0s

running (0m06.0s), 02/10 VUs, 2362 complete and 0 interrupted iterations
default   [  10% ] 02/10 VUs  0m06.0s/1m00.0s

running (0m07.0s), 03/10 VUs, 3154 complete and 0 interrupted iterations
default   [  12% ] 03/10 VUs  0m07.0s/1m00.0s

running (0m08.0s), 03/10 VUs, 4168 complete and 0 interrupted iterations
default   [  13% ] 03/10 VUs  0m08.0s/1m00.0s

running (0m09.0s), 03/10 VUs, 4931 complete and 0 interrupted iterations
default   [  15% ] 03/10 VUs  0m09.0s/1m00.0s

running (0m10.0s), 03/10 VUs, 5912 complete and 0 interrupted iterations
default   [  17% ] 03/10 VUs  0m10.0s/1m00.0s

running (0m11.0s), 04/10 VUs, 7213 complete and 0 interrupted iterations
default   [  18% ] 04/10 VUs  0m11.0s/1m00.0s

running (0m12.0s), 04/10 VUs, 8602 complete and 0 interrupted iterations
default   [  20% ] 04/10 VUs  0m12.0s/1m00.0s

running (0m13.0s), 04/10 VUs, 9979 complete and 0 interrupted iterations
default   [  22% ] 04/10 VUs  0m13.0s/1m00.0s

running (0m14.0s), 05/10 VUs, 11506 complete and 0 interrupted iterations
default   [  23% ] 05/10 VUs  0m14.0s/1m00.0s

running (0m15.0s), 05/10 VUs, 12928 complete and 0 interrupted iterations
default   [  25% ] 05/10 VUs  0m15.0s/1m00.0s

running (0m16.0s), 05/10 VUs, 14472 complete and 0 interrupted iterations
default   [  27% ] 05/10 VUs  0m16.0s/1m00.0s

running (0m17.0s), 06/10 VUs, 15550 complete and 0 interrupted iterations
default   [  28% ] 06/10 VUs  0m17.0s/1m00.0s

running (0m18.0s), 06/10 VUs, 16478 complete and 0 interrupted iterations
default   [  30% ] 06/10 VUs  0m18.0s/1m00.0s

running (0m19.0s), 06/10 VUs, 18181 complete and 0 interrupted iterations
default   [  32% ] 06/10 VUs  0m19.0s/1m00.0s

running (0m20.0s), 06/10 VUs, 19921 complete and 0 interrupted iterations
default   [  33% ] 06/10 VUs  0m20.0s/1m00.0s

running (0m21.0s), 07/10 VUs, 21300 complete and 0 interrupted iterations
default   [  35% ] 07/10 VUs  0m21.0s/1m00.0s

running (0m22.0s), 07/10 VUs, 23302 complete and 0 interrupted iterations
default   [  37% ] 07/10 VUs  0m22.0s/1m00.0s

running (0m23.0s), 07/10 VUs, 25350 complete and 0 interrupted iterations
default   [  38% ] 07/10 VUs  0m23.0s/1m00.0s

running (0m24.0s), 08/10 VUs, 27357 complete and 0 interrupted iterations
default   [  40% ] 08/10 VUs  0m24.0s/1m00.0s

running (0m25.0s), 08/10 VUs, 29281 complete and 0 interrupted iterations
default   [  42% ] 08/10 VUs  0m25.0s/1m00.0s

running (0m26.0s), 08/10 VUs, 31346 complete and 0 interrupted iterations
default   [  43% ] 08/10 VUs  0m26.0s/1m00.0s

running (0m27.0s), 09/10 VUs, 33340 complete and 0 interrupted iterations
default   [  45% ] 09/10 VUs  0m27.0s/1m00.0s

running (0m28.0s), 09/10 VUs, 35520 complete and 0 interrupted iterations
default   [  47% ] 09/10 VUs  0m28.0s/1m00.0s

running (0m29.0s), 09/10 VUs, 37731 complete and 0 interrupted iterations
default   [  48% ] 09/10 VUs  0m29.0s/1m00.0s

running (0m30.0s), 09/10 VUs, 39900 complete and 0 interrupted iterations
default   [  50% ] 09/10 VUs  0m30.0s/1m00.0s

running (0m31.0s), 10/10 VUs, 41985 complete and 0 interrupted iterations
default   [  52% ] 10/10 VUs  0m31.0s/1m00.0s

running (0m32.0s), 10/10 VUs, 44185 complete and 0 interrupted iterations
default   [  53% ] 10/10 VUs  0m32.0s/1m00.0s

running (0m33.0s), 10/10 VUs, 46443 complete and 0 interrupted iterations
default   [  55% ] 10/10 VUs  0m33.0s/1m00.0s

running (0m34.0s), 10/10 VUs, 48668 complete and 0 interrupted iterations
default   [  57% ] 10/10 VUs  0m34.0s/1m00.0s

running (0m35.0s), 10/10 VUs, 50796 complete and 0 interrupted iterations
default   [  58% ] 10/10 VUs  0m35.0s/1m00.0s

running (0m36.0s), 10/10 VUs, 52935 complete and 0 interrupted iterations
default   [  60% ] 10/10 VUs  0m36.0s/1m00.0s

running (0m37.0s), 10/10 VUs, 54994 complete and 0 interrupted iterations
default   [  62% ] 10/10 VUs  0m37.0s/1m00.0s

running (0m38.0s), 10/10 VUs, 57028 complete and 0 interrupted iterations
default   [  63% ] 10/10 VUs  0m38.0s/1m00.0s

running (0m39.0s), 10/10 VUs, 59239 complete and 0 interrupted iterations
default   [  65% ] 10/10 VUs  0m39.0s/1m00.0s

running (0m40.0s), 10/10 VUs, 61341 complete and 0 interrupted iterations
default   [  67% ] 10/10 VUs  0m40.0s/1m00.0s

running (0m41.0s), 10/10 VUs, 63307 complete and 0 interrupted iterations
default   [  68% ] 10/10 VUs  0m41.0s/1m00.0s

running (0m42.0s), 10/10 VUs, 65555 complete and 0 interrupted iterations
default   [  70% ] 10/10 VUs  0m42.0s/1m00.0s

running (0m43.0s), 10/10 VUs, 67736 complete and 0 interrupted iterations
default   [  72% ] 10/10 VUs  0m43.0s/1m00.0s

running (0m44.0s), 10/10 VUs, 70014 complete and 0 interrupted iterations
default   [  73% ] 10/10 VUs  0m44.0s/1m00.0s

running (0m45.0s), 10/10 VUs, 71584 complete and 0 interrupted iterations
default   [  75% ] 10/10 VUs  0m45.0s/1m00.0s

running (0m46.0s), 10/10 VUs, 73702 complete and 0 interrupted iterations
default   [  77% ] 10/10 VUs  0m46.0s/1m00.0s

running (0m47.0s), 10/10 VUs, 75726 complete and 0 interrupted iterations
default   [  78% ] 10/10 VUs  0m47.0s/1m00.0s

running (0m48.0s), 10/10 VUs, 77911 complete and 0 interrupted iterations
default   [  80% ] 10/10 VUs  0m48.0s/1m00.0s

running (0m49.0s), 10/10 VUs, 80129 complete and 0 interrupted iterations
default   [  82% ] 10/10 VUs  0m49.0s/1m00.0s

running (0m50.0s), 10/10 VUs, 82335 complete and 0 interrupted iterations
default   [  83% ] 10/10 VUs  0m50.0s/1m00.0s

running (0m51.0s), 10/10 VUs, 84528 complete and 0 interrupted iterations
default   [  85% ] 10/10 VUs  0m51.0s/1m00.0s

running (0m52.0s), 10/10 VUs, 86710 complete and 0 interrupted iterations
default   [  87% ] 10/10 VUs  0m52.0s/1m00.0s

running (0m53.0s), 10/10 VUs, 88888 complete and 0 interrupted iterations
default   [  88% ] 10/10 VUs  0m53.0s/1m00.0s

running (0m54.0s), 10/10 VUs, 91081 complete and 0 interrupted iterations
default   [  90% ] 10/10 VUs  0m54.0s/1m00.0s

running (0m55.0s), 10/10 VUs, 93292 complete and 0 interrupted iterations
default   [  92% ] 10/10 VUs  0m55.0s/1m00.0s

running (0m56.0s), 10/10 VUs, 95476 complete and 0 interrupted iterations
default   [  93% ] 10/10 VUs  0m56.0s/1m00.0s

running (0m57.0s), 10/10 VUs, 97376 complete and 0 interrupted iterations
default   [  95% ] 10/10 VUs  0m57.0s/1m00.0s

running (0m58.0s), 10/10 VUs, 99372 complete and 0 interrupted iterations
default   [  97% ] 10/10 VUs  0m58.0s/1m00.0s

running (0m59.0s), 10/10 VUs, 101343 complete and 0 interrupted iterations
default   [  98% ] 10/10 VUs  0m59.0s/1m00.0s

running (1m00.0s), 10/10 VUs, 102472 complete and 0 interrupted iterations
default   [ 100% ] 10/10 VUs  1m00.0s/1m00.0s


  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(95)<200' p(95)=6.7ms
    ✓ 'p(99)<500' p(99)=11.57ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.00%


  █ TOTAL RESULTS 

    checks_total.......................: 614952 10248.631543/s
    checks_succeeded...................: 99.99% 614947 out of 614952
    checks_failed......................: 0.00%  5 out of 614952

    ✓ ingest event status is 201
    ✓ ingest event has eventId in response
    ✓ ingest event has status in response
    ✓ ingest event has processedAt in response
    ✗ smoke: response time < 200ms
      ↳  99% — ✓ 102487 / ✗ 5
    ✓ smoke: response has body

    HTTP
    http_req_duration.......................................................: avg=4.27ms min=1.82ms med=3.84ms max=248.34ms p(90)=5.71ms p(95)=6.7ms 
      { expected_response:true }............................................: avg=4.27ms min=1.82ms med=3.84ms max=248.34ms p(90)=5.71ms p(95)=6.7ms 
    http_req_failed.........................................................: 0.00%  0 out of 102492
    http_reqs...............................................................: 102492 1708.105257/s

    EXECUTION
    iteration_duration......................................................: avg=4.38ms min=1.9ms  med=3.94ms max=248.47ms p(90)=5.81ms p(95)=6.81ms
    iterations..............................................................: 102492 1708.105257/s
    vus.....................................................................: 10     min=1           max=10
    vus_max.................................................................: 10     min=10          max=10

    NETWORK
    data_received...........................................................: 19 MB  311 kB/s
    data_sent...............................................................: 41 MB  688 kB/s




running (1m00.0s), 00/10 VUs, 102492 complete and 0 interrupted iterations
default ✓ [ 100% ] 00/10 VUs  1m0s


## Load Test - 2025-11-13 21:23:43


         /\      Grafana   /‾‾/  
    /\  /  \     |\  __   /  /   
   /  \/    \    | |/ /  /   ‾‾\ 
  /          \   |   (  |  (‾)  |
 / __________ \  |_|\_\  \_____/ 

     execution: local
        script: k6/scripts/load-test.js
        output: -

     scenarios: (100.00%) 1 scenario, 350 max VUs, 2m30s max duration (incl. graceful stop):
              * default: Up to 350 looping VUs for 2m0s over 2 stages (gracefulRampDown: 30s, gracefulStop: 30s)


running (0m01.0s), 012/350 VUs, 1339 complete and 0 interrupted iterations
default   [   1% ] 012/350 VUs  0m01.0s/2m00.0s

running (0m02.0s), 023/350 VUs, 3930 complete and 0 interrupted iterations
default   [   2% ] 023/350 VUs  0m02.0s/2m00.0s

running (0m03.0s), 035/350 VUs, 7039 complete and 0 interrupted iterations
default   [   2% ] 035/350 VUs  0m03.0s/2m00.0s

running (0m04.0s), 047/350 VUs, 9913 complete and 0 interrupted iterations
default   [   3% ] 047/350 VUs  0m04.0s/2m00.0s

running (0m05.0s), 058/350 VUs, 13021 complete and 0 interrupted iterations
default   [   4% ] 058/350 VUs  0m05.0s/2m00.0s

running (0m06.0s), 070/350 VUs, 16754 complete and 0 interrupted iterations
default   [   5% ] 070/350 VUs  0m06.0s/2m00.0s

running (0m07.0s), 082/350 VUs, 20464 complete and 0 interrupted iterations
default   [   6% ] 082/350 VUs  0m07.0s/2m00.0s

running (0m08.0s), 093/350 VUs, 24470 complete and 0 interrupted iterations
default   [   7% ] 093/350 VUs  0m08.0s/2m00.0s

running (0m09.0s), 105/350 VUs, 28373 complete and 0 interrupted iterations
default   [   7% ] 105/350 VUs  0m09.0s/2m00.0s

running (0m10.0s), 117/350 VUs, 32088 complete and 0 interrupted iterations
default   [   8% ] 117/350 VUs  0m10.0s/2m00.0s

running (0m11.0s), 128/350 VUs, 36070 complete and 0 interrupted iterations
default   [   9% ] 128/350 VUs  0m11.0s/2m00.0s

running (0m12.0s), 140/350 VUs, 39862 complete and 0 interrupted iterations
default   [  10% ] 140/350 VUs  0m12.0s/2m00.0s

running (0m13.0s), 151/350 VUs, 43733 complete and 0 interrupted iterations
default   [  11% ] 151/350 VUs  0m13.0s/2m00.0s

running (0m14.0s), 163/350 VUs, 47670 complete and 0 interrupted iterations
default   [  12% ] 163/350 VUs  0m14.0s/2m00.0s

running (0m15.0s), 175/350 VUs, 51324 complete and 0 interrupted iterations
default   [  12% ] 175/350 VUs  0m15.0s/2m00.0s

running (0m16.0s), 186/350 VUs, 55373 complete and 0 interrupted iterations
default   [  13% ] 186/350 VUs  0m16.0s/2m00.0s

running (0m17.0s), 198/350 VUs, 59073 complete and 0 interrupted iterations
default   [  14% ] 198/350 VUs  0m17.0s/2m00.0s

running (0m18.0s), 210/350 VUs, 63206 complete and 0 interrupted iterations
default   [  15% ] 210/350 VUs  0m18.0s/2m00.0s

running (0m19.0s), 221/350 VUs, 67116 complete and 0 interrupted iterations
default   [  16% ] 221/350 VUs  0m19.0s/2m00.0s

running (0m20.0s), 233/350 VUs, 70842 complete and 0 interrupted iterations
default   [  17% ] 233/350 VUs  0m20.0s/2m00.0s

running (0m21.0s), 244/350 VUs, 72068 complete and 0 interrupted iterations
default   [  17% ] 244/350 VUs  0m21.0s/2m00.0s

running (0m22.0s), 256/350 VUs, 74599 complete and 0 interrupted iterations
default   [  18% ] 256/350 VUs  0m22.0s/2m00.0s

running (0m23.0s), 268/350 VUs, 78229 complete and 0 interrupted iterations
default   [  19% ] 268/350 VUs  0m23.0s/2m00.0s

running (0m24.0s), 279/350 VUs, 81781 complete and 0 interrupted iterations
default   [  20% ] 279/350 VUs  0m24.0s/2m00.0s

running (0m25.0s), 291/350 VUs, 85512 complete and 0 interrupted iterations
default   [  21% ] 291/350 VUs  0m25.0s/2m00.0s

running (0m26.0s), 303/350 VUs, 89030 complete and 0 interrupted iterations
default   [  22% ] 303/350 VUs  0m26.0s/2m00.0s

running (0m27.0s), 314/350 VUs, 92535 complete and 0 interrupted iterations
default   [  22% ] 314/350 VUs  0m27.0s/2m00.0s

running (0m28.0s), 326/350 VUs, 96195 complete and 0 interrupted iterations
default   [  23% ] 326/350 VUs  0m28.0s/2m00.0s

running (0m29.0s), 338/350 VUs, 100320 complete and 0 interrupted iterations
default   [  24% ] 338/350 VUs  0m29.0s/2m00.0s

running (0m30.0s), 349/350 VUs, 104233 complete and 0 interrupted iterations
default   [  25% ] 349/350 VUs  0m30.0s/2m00.0s

running (0m31.0s), 350/350 VUs, 108462 complete and 0 interrupted iterations
default   [  26% ] 350/350 VUs  0m31.0s/2m00.0s

running (0m32.0s), 350/350 VUs, 111854 complete and 0 interrupted iterations
default   [  27% ] 350/350 VUs  0m32.0s/2m00.0s

running (0m33.0s), 350/350 VUs, 115244 complete and 0 interrupted iterations
default   [  27% ] 350/350 VUs  0m33.0s/2m00.0s

running (0m34.0s), 350/350 VUs, 119161 complete and 0 interrupted iterations
default   [  28% ] 350/350 VUs  0m34.0s/2m00.0s

running (0m35.0s), 350/350 VUs, 120211 complete and 0 interrupted iterations
default   [  29% ] 350/350 VUs  0m35.0s/2m00.0s

running (0m36.0s), 350/350 VUs, 123980 complete and 0 interrupted iterations
default   [  30% ] 350/350 VUs  0m36.0s/2m00.0s

running (0m37.0s), 350/350 VUs, 127847 complete and 0 interrupted iterations
default   [  31% ] 350/350 VUs  0m37.0s/2m00.0s

running (0m38.0s), 350/350 VUs, 130955 complete and 0 interrupted iterations
default   [  32% ] 350/350 VUs  0m38.0s/2m00.0s

running (0m39.0s), 350/350 VUs, 134646 complete and 0 interrupted iterations
default   [  32% ] 350/350 VUs  0m39.0s/2m00.0s

running (0m40.0s), 350/350 VUs, 138453 complete and 0 interrupted iterations
default   [  33% ] 350/350 VUs  0m40.0s/2m00.0s

running (0m41.0s), 350/350 VUs, 142339 complete and 0 interrupted iterations
default   [  34% ] 350/350 VUs  0m41.0s/2m00.0s

running (0m42.0s), 350/350 VUs, 145903 complete and 0 interrupted iterations
default   [  35% ] 350/350 VUs  0m42.0s/2m00.0s

running (0m43.0s), 350/350 VUs, 149805 complete and 0 interrupted iterations
default   [  36% ] 350/350 VUs  0m43.0s/2m00.0s

running (0m44.0s), 350/350 VUs, 153630 complete and 0 interrupted iterations
default   [  37% ] 350/350 VUs  0m44.0s/2m00.0s

running (0m45.0s), 350/350 VUs, 156556 complete and 0 interrupted iterations
default   [  37% ] 350/350 VUs  0m45.0s/2m00.0s

running (0m46.0s), 350/350 VUs, 160584 complete and 0 interrupted iterations
default   [  38% ] 350/350 VUs  0m46.0s/2m00.0s

running (0m47.0s), 350/350 VUs, 164324 complete and 0 interrupted iterations
default   [  39% ] 350/350 VUs  0m47.0s/2m00.0s

running (0m48.0s), 350/350 VUs, 168155 complete and 0 interrupted iterations
default   [  40% ] 350/350 VUs  0m48.0s/2m00.0s

running (0m49.0s), 350/350 VUs, 172064 complete and 0 interrupted iterations
default   [  41% ] 350/350 VUs  0m49.0s/2m00.0s

running (0m50.0s), 350/350 VUs, 175990 complete and 0 interrupted iterations
default   [  42% ] 350/350 VUs  0m50.0s/2m00.0s

running (0m51.0s), 350/350 VUs, 179806 complete and 0 interrupted iterations
default   [  42% ] 350/350 VUs  0m51.0s/2m00.0s

running (0m52.0s), 350/350 VUs, 183562 complete and 0 interrupted iterations
default   [  43% ] 350/350 VUs  0m52.0s/2m00.0s

running (0m53.0s), 350/350 VUs, 187686 complete and 0 interrupted iterations
default   [  44% ] 350/350 VUs  0m53.0s/2m00.0s

running (0m54.0s), 350/350 VUs, 191487 complete and 0 interrupted iterations
default   [  45% ] 350/350 VUs  0m54.0s/2m00.0s

running (0m55.0s), 350/350 VUs, 195204 complete and 0 interrupted iterations
default   [  46% ] 350/350 VUs  0m55.0s/2m00.0s

running (0m56.0s), 350/350 VUs, 199406 complete and 0 interrupted iterations
default   [  47% ] 350/350 VUs  0m56.0s/2m00.0s

running (0m57.0s), 350/350 VUs, 203407 complete and 0 interrupted iterations
default   [  47% ] 350/350 VUs  0m57.0s/2m00.0s

running (0m58.0s), 350/350 VUs, 207284 complete and 0 interrupted iterations
default   [  48% ] 350/350 VUs  0m58.0s/2m00.0s

running (0m59.0s), 350/350 VUs, 211227 complete and 0 interrupted iterations
default   [  49% ] 350/350 VUs  0m59.0s/2m00.0s

running (1m00.0s), 350/350 VUs, 213711 complete and 0 interrupted iterations
default   [  50% ] 350/350 VUs  1m00.0s/2m00.0s

running (1m01.0s), 350/350 VUs, 217640 complete and 0 interrupted iterations
default   [  51% ] 350/350 VUs  1m01.0s/2m00.0s

running (1m02.0s), 350/350 VUs, 221657 complete and 0 interrupted iterations
default   [  52% ] 350/350 VUs  1m02.0s/2m00.0s

running (1m03.0s), 350/350 VUs, 225459 complete and 0 interrupted iterations
default   [  52% ] 350/350 VUs  1m03.0s/2m00.0s

running (1m04.0s), 350/350 VUs, 229295 complete and 0 interrupted iterations
default   [  53% ] 350/350 VUs  1m04.0s/2m00.0s

running (1m05.0s), 350/350 VUs, 233318 complete and 0 interrupted iterations
default   [  54% ] 350/350 VUs  1m05.0s/2m00.0s

running (1m06.0s), 350/350 VUs, 237668 complete and 0 interrupted iterations
default   [  55% ] 350/350 VUs  1m06.0s/2m00.0s

running (1m07.0s), 350/350 VUs, 241612 complete and 0 interrupted iterations
default   [  56% ] 350/350 VUs  1m07.0s/2m00.0s

running (1m08.0s), 350/350 VUs, 245929 complete and 0 interrupted iterations
default   [  57% ] 350/350 VUs  1m08.0s/2m00.0s

running (1m09.0s), 350/350 VUs, 249814 complete and 0 interrupted iterations
default   [  57% ] 350/350 VUs  1m09.0s/2m00.0s

running (1m10.0s), 350/350 VUs, 253786 complete and 0 interrupted iterations
default   [  58% ] 350/350 VUs  1m10.0s/2m00.0s

running (1m11.0s), 350/350 VUs, 257851 complete and 0 interrupted iterations
default   [  59% ] 350/350 VUs  1m11.0s/2m00.0s

running (1m12.0s), 350/350 VUs, 261754 complete and 0 interrupted iterations
default   [  60% ] 350/350 VUs  1m12.0s/2m00.0s

running (1m13.0s), 350/350 VUs, 265731 complete and 0 interrupted iterations
default   [  61% ] 350/350 VUs  1m13.0s/2m00.0s

running (1m14.0s), 350/350 VUs, 269493 complete and 0 interrupted iterations
default   [  62% ] 350/350 VUs  1m14.0s/2m00.0s

running (1m15.0s), 350/350 VUs, 273098 complete and 0 interrupted iterations
default   [  62% ] 350/350 VUs  1m15.0s/2m00.0s

running (1m16.0s), 350/350 VUs, 276894 complete and 0 interrupted iterations
default   [  63% ] 350/350 VUs  1m16.0s/2m00.0s

running (1m17.0s), 350/350 VUs, 280736 complete and 0 interrupted iterations
default   [  64% ] 350/350 VUs  1m17.0s/2m00.0s

running (1m18.0s), 350/350 VUs, 284624 complete and 0 interrupted iterations
default   [  65% ] 350/350 VUs  1m18.0s/2m00.0s

running (1m19.0s), 350/350 VUs, 288213 complete and 0 interrupted iterations
default   [  66% ] 350/350 VUs  1m19.0s/2m00.0s

running (1m20.0s), 350/350 VUs, 292029 complete and 0 interrupted iterations
default   [  67% ] 350/350 VUs  1m20.0s/2m00.0s

running (1m21.0s), 350/350 VUs, 294097 complete and 0 interrupted iterations
default   [  67% ] 350/350 VUs  1m21.0s/2m00.0s

running (1m22.0s), 350/350 VUs, 295748 complete and 0 interrupted iterations
default   [  68% ] 350/350 VUs  1m22.0s/2m00.0s

running (1m23.0s), 350/350 VUs, 299045 complete and 0 interrupted iterations
default   [  69% ] 350/350 VUs  1m23.0s/2m00.0s

running (1m24.0s), 350/350 VUs, 303458 complete and 0 interrupted iterations
default   [  70% ] 350/350 VUs  1m24.0s/2m00.0s

running (1m25.0s), 350/350 VUs, 307648 complete and 0 interrupted iterations
default   [  71% ] 350/350 VUs  1m25.0s/2m00.0s

running (1m26.0s), 350/350 VUs, 312017 complete and 0 interrupted iterations
default   [  72% ] 350/350 VUs  1m26.0s/2m00.0s

running (1m27.0s), 350/350 VUs, 316618 complete and 0 interrupted iterations
default   [  72% ] 350/350 VUs  1m27.0s/2m00.0s

running (1m28.0s), 350/350 VUs, 321022 complete and 0 interrupted iterations
default   [  73% ] 350/350 VUs  1m28.0s/2m00.0s

running (1m29.0s), 350/350 VUs, 325382 complete and 0 interrupted iterations
default   [  74% ] 350/350 VUs  1m29.0s/2m00.0s

running (1m30.0s), 350/350 VUs, 329635 complete and 0 interrupted iterations
default   [  75% ] 350/350 VUs  1m30.0s/2m00.0s

running (1m31.0s), 350/350 VUs, 333777 complete and 0 interrupted iterations
default   [  76% ] 350/350 VUs  1m31.0s/2m00.0s

running (1m32.0s), 350/350 VUs, 337022 complete and 0 interrupted iterations
default   [  77% ] 350/350 VUs  1m32.0s/2m00.0s

running (1m33.0s), 350/350 VUs, 341450 complete and 0 interrupted iterations
default   [  77% ] 350/350 VUs  1m33.0s/2m00.0s

running (1m34.0s), 350/350 VUs, 345586 complete and 0 interrupted iterations
default   [  78% ] 350/350 VUs  1m34.0s/2m00.0s

running (1m35.0s), 350/350 VUs, 349804 complete and 0 interrupted iterations
default   [  79% ] 350/350 VUs  1m35.0s/2m00.0s

running (1m36.0s), 350/350 VUs, 354073 complete and 0 interrupted iterations
default   [  80% ] 350/350 VUs  1m36.0s/2m00.0s

running (1m37.0s), 350/350 VUs, 358403 complete and 0 interrupted iterations
default   [  81% ] 350/350 VUs  1m37.0s/2m00.0s

running (1m38.0s), 350/350 VUs, 362413 complete and 0 interrupted iterations
default   [  82% ] 350/350 VUs  1m38.0s/2m00.0s

running (1m39.0s), 350/350 VUs, 366743 complete and 0 interrupted iterations
default   [  82% ] 350/350 VUs  1m39.0s/2m00.0s

running (1m40.0s), 350/350 VUs, 370813 complete and 0 interrupted iterations
default   [  83% ] 350/350 VUs  1m40.0s/2m00.0s

running (1m41.0s), 350/350 VUs, 374855 complete and 0 interrupted iterations
default   [  84% ] 350/350 VUs  1m41.0s/2m00.0s

running (1m42.0s), 350/350 VUs, 378943 complete and 0 interrupted iterations
default   [  85% ] 350/350 VUs  1m42.0s/2m00.0s

running (1m43.0s), 350/350 VUs, 383194 complete and 0 interrupted iterations
default   [  86% ] 350/350 VUs  1m43.0s/2m00.0s

running (1m44.0s), 350/350 VUs, 387371 complete and 0 interrupted iterations
default   [  87% ] 350/350 VUs  1m44.0s/2m00.0s

running (1m45.0s), 350/350 VUs, 391541 complete and 0 interrupted iterations
default   [  87% ] 350/350 VUs  1m45.0s/2m00.0s

running (1m46.0s), 350/350 VUs, 396093 complete and 0 interrupted iterations
default   [  88% ] 350/350 VUs  1m46.0s/2m00.0s

running (1m47.0s), 350/350 VUs, 400332 complete and 0 interrupted iterations
default   [  89% ] 350/350 VUs  1m47.0s/2m00.0s

running (1m48.0s), 350/350 VUs, 404700 complete and 0 interrupted iterations
default   [  90% ] 350/350 VUs  1m48.0s/2m00.0s

running (1m49.0s), 350/350 VUs, 409236 complete and 0 interrupted iterations
default   [  91% ] 350/350 VUs  1m49.0s/2m00.0s

running (1m50.0s), 350/350 VUs, 413308 complete and 0 interrupted iterations
default   [  92% ] 350/350 VUs  1m50.0s/2m00.0s

running (1m51.0s), 350/350 VUs, 417270 complete and 0 interrupted iterations
default   [  92% ] 350/350 VUs  1m51.0s/2m00.0s

running (1m52.0s), 350/350 VUs, 421396 complete and 0 interrupted iterations
default   [  93% ] 350/350 VUs  1m52.0s/2m00.0s

running (1m53.0s), 350/350 VUs, 425981 complete and 0 interrupted iterations
default   [  94% ] 350/350 VUs  1m53.0s/2m00.0s

running (1m54.0s), 350/350 VUs, 430274 complete and 0 interrupted iterations
default   [  95% ] 350/350 VUs  1m54.0s/2m00.0s

running (1m55.0s), 350/350 VUs, 434336 complete and 0 interrupted iterations
default   [  96% ] 350/350 VUs  1m55.0s/2m00.0s

running (1m56.0s), 350/350 VUs, 438618 complete and 0 interrupted iterations
default   [  97% ] 350/350 VUs  1m56.0s/2m00.0s

running (1m57.0s), 350/350 VUs, 442755 complete and 0 interrupted iterations
default   [  97% ] 350/350 VUs  1m57.0s/2m00.0s

running (1m58.0s), 350/350 VUs, 447123 complete and 0 interrupted iterations
default   [  98% ] 350/350 VUs  1m58.0s/2m00.0s

running (1m59.0s), 350/350 VUs, 451459 complete and 0 interrupted iterations
default   [  99% ] 350/350 VUs  1m59.0s/2m00.0s

running (2m00.0s), 350/350 VUs, 455486 complete and 0 interrupted iterations
default   [ 100% ] 350/350 VUs  2m00.0s/2m00.0s


  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(95)<500' p(95)=138.96ms
    ✓ 'p(99)<1000' p(99)=209.3ms

    http_req_failed
    ✓ 'rate<0.001' rate=0.00%

    http_reqs
    ✓ 'rate>2000' rate=3797.680751/s


  █ TOTAL RESULTS 

    checks_total.......................: 2735658 22786.084508/s
    checks_succeeded...................: 99.97%  2734933 out of 2735658
    checks_failed......................: 0.02%   725 out of 2735658

    ✓ ingest event status is 201
    ✓ ingest event has eventId in response
    ✓ ingest event has status in response
    ✓ ingest event has processedAt in response
    ✗ load: response time < 500ms
      ↳  99% — ✓ 455218 / ✗ 725
    ✓ load: response has body

    HTTP
    http_req_duration.......................................................: avg=80.37ms min=2.06ms med=78.82ms max=741.31ms p(90)=115.98ms p(95)=138.96ms
      { expected_response:true }............................................: avg=80.37ms min=2.06ms med=78.82ms max=741.31ms p(90)=115.98ms p(95)=138.96ms
    http_req_failed.........................................................: 0.00%  0 out of 455943
    http_reqs...............................................................: 455943 3797.680751/s

    EXECUTION
    iteration_duration......................................................: avg=80.61ms min=2.16ms med=79.02ms max=741.38ms p(90)=116.3ms  p(95)=139.37ms
    iterations..............................................................: 455943 3797.680751/s
    vus.....................................................................: 350    min=12          max=350
    vus_max.................................................................: 350    min=350         max=350

    NETWORK
    data_received...........................................................: 83 MB  691 kB/s
    data_sent...............................................................: 184 MB 1.5 MB/s




running (2m00.1s), 000/350 VUs, 455943 complete and 0 interrupted iterations
default ✓ [ 100% ] 000/350 VUs  2m0s


## Stress Test - 2025-11-13 21:27:08


         /\      Grafana   /‾‾/  
    /\  /  \     |\  __   /  /   
   /  \/    \    | |/ /  /   ‾‾\ 
  /          \   |   (  |  (‾)  |
 / __________ \  |_|\_\  \_____/ 

     execution: local
        script: k6/scripts/stress-test.js
        output: -

     scenarios: (100.00%) 1 scenario, 500 max VUs, 3m30s max duration (incl. graceful stop):
              * default: Up to 500 looping VUs for 3m0s over 4 stages (gracefulRampDown: 30s, gracefulStop: 30s)


running (0m01.0s), 002/500 VUs, 400 complete and 0 interrupted iterations
default   [   1% ] 002/500 VUs  0m01.0s/3m00.0s

running (0m02.0s), 004/500 VUs, 1082 complete and 0 interrupted iterations
default   [   1% ] 004/500 VUs  0m02.0s/3m00.0s

running (0m03.0s), 005/500 VUs, 2366 complete and 0 interrupted iterations
default   [   2% ] 005/500 VUs  0m03.0s/3m00.0s

running (0m04.0s), 007/500 VUs, 4165 complete and 0 interrupted iterations
default   [   2% ] 007/500 VUs  0m04.0s/3m00.0s

running (0m05.0s), 009/500 VUs, 6239 complete and 0 interrupted iterations
default   [   3% ] 009/500 VUs  0m05.0s/3m00.0s

running (0m06.0s), 010/500 VUs, 8352 complete and 0 interrupted iterations
default   [   3% ] 010/500 VUs  0m06.0s/3m00.0s

running (0m07.0s), 012/500 VUs, 8792 complete and 0 interrupted iterations
default   [   4% ] 012/500 VUs  0m07.0s/3m00.0s

running (0m08.0s), 014/500 VUs, 10324 complete and 0 interrupted iterations
default   [   4% ] 014/500 VUs  0m08.0s/3m00.0s

running (0m09.0s), 015/500 VUs, 12331 complete and 0 interrupted iterations
default   [   5% ] 015/500 VUs  0m09.0s/3m00.0s

running (0m10.0s), 017/500 VUs, 14302 complete and 0 interrupted iterations
default   [   6% ] 017/500 VUs  0m10.0s/3m00.0s

running (0m11.0s), 018/500 VUs, 16169 complete and 0 interrupted iterations
default   [   6% ] 018/500 VUs  0m11.0s/3m00.0s

running (0m12.0s), 020/500 VUs, 18709 complete and 0 interrupted iterations
default   [   7% ] 020/500 VUs  0m12.0s/3m00.0s

running (0m13.0s), 022/500 VUs, 21176 complete and 0 interrupted iterations
default   [   7% ] 022/500 VUs  0m13.0s/3m00.0s

running (0m14.0s), 023/500 VUs, 23687 complete and 0 interrupted iterations
default   [   8% ] 023/500 VUs  0m14.0s/3m00.0s

running (0m15.0s), 025/500 VUs, 26028 complete and 0 interrupted iterations
default   [   8% ] 025/500 VUs  0m15.0s/3m00.0s

running (0m16.0s), 027/500 VUs, 28455 complete and 0 interrupted iterations
default   [   9% ] 027/500 VUs  0m16.0s/3m00.0s

running (0m17.0s), 028/500 VUs, 30971 complete and 0 interrupted iterations
default   [   9% ] 028/500 VUs  0m17.0s/3m00.0s

running (0m18.0s), 030/500 VUs, 33607 complete and 0 interrupted iterations
default   [  10% ] 030/500 VUs  0m18.0s/3m00.0s

running (0m19.0s), 031/500 VUs, 36200 complete and 0 interrupted iterations
default   [  11% ] 031/500 VUs  0m19.0s/3m00.0s

running (0m20.0s), 033/500 VUs, 38820 complete and 0 interrupted iterations
default   [  11% ] 033/500 VUs  0m20.0s/3m00.0s

running (0m21.0s), 035/500 VUs, 41436 complete and 0 interrupted iterations
default   [  12% ] 035/500 VUs  0m21.0s/3m00.0s

running (0m22.0s), 036/500 VUs, 44072 complete and 0 interrupted iterations
default   [  12% ] 036/500 VUs  0m22.0s/3m00.0s

running (0m23.0s), 038/500 VUs, 46810 complete and 0 interrupted iterations
default   [  13% ] 038/500 VUs  0m23.0s/3m00.0s

running (0m24.0s), 040/500 VUs, 49536 complete and 0 interrupted iterations
default   [  13% ] 040/500 VUs  0m24.0s/3m00.0s

running (0m25.0s), 041/500 VUs, 52170 complete and 0 interrupted iterations
default   [  14% ] 041/500 VUs  0m25.0s/3m00.0s

running (0m26.0s), 043/500 VUs, 54612 complete and 0 interrupted iterations
default   [  14% ] 043/500 VUs  0m26.0s/3m00.0s

running (0m27.0s), 045/500 VUs, 57262 complete and 0 interrupted iterations
default   [  15% ] 045/500 VUs  0m27.0s/3m00.0s

running (0m28.0s), 046/500 VUs, 60098 complete and 0 interrupted iterations
default   [  16% ] 046/500 VUs  0m28.0s/3m00.0s

running (0m29.0s), 048/500 VUs, 62891 complete and 0 interrupted iterations
default   [  16% ] 048/500 VUs  0m29.0s/3m00.0s

running (0m30.0s), 049/500 VUs, 65554 complete and 0 interrupted iterations
default   [  17% ] 049/500 VUs  0m30.0s/3m00.0s

running (0m31.0s), 054/500 VUs, 68407 complete and 0 interrupted iterations
default   [  17% ] 054/500 VUs  0m31.0s/3m00.0s

running (0m32.0s), 059/500 VUs, 71240 complete and 0 interrupted iterations
default   [  18% ] 059/500 VUs  0m32.0s/3m00.0s

running (0m33.0s), 064/500 VUs, 74087 complete and 0 interrupted iterations
default   [  18% ] 064/500 VUs  0m33.0s/3m00.0s

running (0m34.0s), 069/500 VUs, 77010 complete and 0 interrupted iterations
default   [  19% ] 069/500 VUs  0m34.0s/3m00.0s

running (0m35.0s), 074/500 VUs, 79725 complete and 0 interrupted iterations
default   [  19% ] 074/500 VUs  0m35.0s/3m00.0s

running (0m36.0s), 079/500 VUs, 82451 complete and 0 interrupted iterations
default   [  20% ] 079/500 VUs  0m36.0s/3m00.0s

running (0m37.0s), 084/500 VUs, 85307 complete and 0 interrupted iterations
default   [  21% ] 084/500 VUs  0m37.0s/3m00.0s

running (0m38.0s), 089/500 VUs, 88358 complete and 0 interrupted iterations
default   [  21% ] 089/500 VUs  0m38.0s/3m00.0s

running (0m39.0s), 094/500 VUs, 91526 complete and 0 interrupted iterations
default   [  22% ] 094/500 VUs  0m39.0s/3m00.0s

running (0m40.0s), 099/500 VUs, 94528 complete and 0 interrupted iterations
default   [  22% ] 099/500 VUs  0m40.0s/3m00.0s

running (0m41.0s), 104/500 VUs, 97593 complete and 0 interrupted iterations
default   [  23% ] 104/500 VUs  0m41.0s/3m00.0s

running (0m42.0s), 109/500 VUs, 100617 complete and 0 interrupted iterations
default   [  23% ] 109/500 VUs  0m42.0s/3m00.0s

running (0m43.0s), 114/500 VUs, 103720 complete and 0 interrupted iterations
default   [  24% ] 114/500 VUs  0m43.0s/3m00.0s

running (0m44.0s), 119/500 VUs, 105859 complete and 0 interrupted iterations
default   [  24% ] 119/500 VUs  0m44.0s/3m00.0s

running (0m45.0s), 124/500 VUs, 108696 complete and 0 interrupted iterations
default   [  25% ] 124/500 VUs  0m45.0s/3m00.0s

running (0m46.0s), 129/500 VUs, 111685 complete and 0 interrupted iterations
default   [  26% ] 129/500 VUs  0m46.0s/3m00.0s

running (0m47.0s), 134/500 VUs, 114423 complete and 0 interrupted iterations
default   [  26% ] 134/500 VUs  0m47.0s/3m00.0s

running (0m48.0s), 139/500 VUs, 116702 complete and 0 interrupted iterations
default   [  27% ] 139/500 VUs  0m48.0s/3m00.0s

running (0m49.0s), 144/500 VUs, 119467 complete and 0 interrupted iterations
default   [  27% ] 144/500 VUs  0m49.0s/3m00.0s

running (0m50.0s), 149/500 VUs, 120983 complete and 0 interrupted iterations
default   [  28% ] 149/500 VUs  0m50.0s/3m00.0s

running (0m51.0s), 154/500 VUs, 124128 complete and 0 interrupted iterations
default   [  28% ] 154/500 VUs  0m51.0s/3m00.0s

running (0m52.0s), 159/500 VUs, 127398 complete and 0 interrupted iterations
default   [  29% ] 159/500 VUs  0m52.0s/3m00.0s

running (0m53.0s), 164/500 VUs, 130654 complete and 0 interrupted iterations
default   [  29% ] 164/500 VUs  0m53.0s/3m00.0s

running (0m54.0s), 169/500 VUs, 133870 complete and 0 interrupted iterations
default   [  30% ] 169/500 VUs  0m54.0s/3m00.0s

running (0m55.0s), 174/500 VUs, 136284 complete and 0 interrupted iterations
default   [  31% ] 174/500 VUs  0m55.0s/3m00.0s

running (0m56.0s), 179/500 VUs, 138849 complete and 0 interrupted iterations
default   [  31% ] 179/500 VUs  0m56.0s/3m00.0s

running (0m57.0s), 184/500 VUs, 141669 complete and 0 interrupted iterations
default   [  32% ] 184/500 VUs  0m57.0s/3m00.0s

running (0m58.0s), 189/500 VUs, 144745 complete and 0 interrupted iterations
default   [  32% ] 189/500 VUs  0m58.0s/3m00.0s

running (0m59.0s), 194/500 VUs, 147711 complete and 0 interrupted iterations
default   [  33% ] 194/500 VUs  0m59.0s/3m00.0s

running (1m00.0s), 199/500 VUs, 150657 complete and 0 interrupted iterations
default   [  33% ] 199/500 VUs  1m00.0s/3m00.0s

running (1m01.0s), 209/500 VUs, 153027 complete and 0 interrupted iterations
default   [  34% ] 209/500 VUs  1m01.0s/3m00.0s

running (1m02.0s), 219/500 VUs, 156141 complete and 0 interrupted iterations
default   [  34% ] 219/500 VUs  1m02.0s/3m00.0s

running (1m03.0s), 229/500 VUs, 159303 complete and 0 interrupted iterations
default   [  35% ] 229/500 VUs  1m03.0s/3m00.0s

running (1m04.0s), 239/500 VUs, 161734 complete and 0 interrupted iterations
default   [  36% ] 239/500 VUs  1m04.0s/3m00.0s

running (1m05.0s), 249/500 VUs, 164055 complete and 0 interrupted iterations
default   [  36% ] 249/500 VUs  1m05.0s/3m00.0s

running (1m06.0s), 259/500 VUs, 167302 complete and 0 interrupted iterations
default   [  37% ] 259/500 VUs  1m06.0s/3m00.0s

running (1m07.0s), 269/500 VUs, 170196 complete and 0 interrupted iterations
default   [  37% ] 269/500 VUs  1m07.0s/3m00.0s

running (1m08.0s), 279/500 VUs, 172344 complete and 0 interrupted iterations
default   [  38% ] 279/500 VUs  1m08.0s/3m00.0s

running (1m09.0s), 289/500 VUs, 175688 complete and 0 interrupted iterations
default   [  38% ] 289/500 VUs  1m09.0s/3m00.0s

running (1m10.0s), 299/500 VUs, 178925 complete and 0 interrupted iterations
default   [  39% ] 299/500 VUs  1m10.0s/3m00.0s

running (1m11.0s), 309/500 VUs, 182290 complete and 0 interrupted iterations
default   [  39% ] 309/500 VUs  1m11.0s/3m00.0s

running (1m12.0s), 319/500 VUs, 185728 complete and 0 interrupted iterations
default   [  40% ] 319/500 VUs  1m12.0s/3m00.0s

running (1m13.0s), 329/500 VUs, 189158 complete and 0 interrupted iterations
default   [  41% ] 329/500 VUs  1m13.0s/3m00.0s

running (1m14.0s), 339/500 VUs, 192351 complete and 0 interrupted iterations
default   [  41% ] 339/500 VUs  1m14.0s/3m00.0s

running (1m15.0s), 349/500 VUs, 195463 complete and 0 interrupted iterations
default   [  42% ] 349/500 VUs  1m15.0s/3m00.0s

running (1m16.0s), 359/500 VUs, 198801 complete and 0 interrupted iterations
default   [  42% ] 359/500 VUs  1m16.0s/3m00.0s

running (1m17.0s), 369/500 VUs, 202251 complete and 0 interrupted iterations
default   [  43% ] 369/500 VUs  1m17.0s/3m00.0s

running (1m18.0s), 379/500 VUs, 205596 complete and 0 interrupted iterations
default   [  43% ] 379/500 VUs  1m18.0s/3m00.0s

running (1m19.0s), 389/500 VUs, 208958 complete and 0 interrupted iterations
default   [  44% ] 389/500 VUs  1m19.0s/3m00.0s

running (1m20.0s), 399/500 VUs, 212256 complete and 0 interrupted iterations
default   [  44% ] 399/500 VUs  1m20.0s/3m00.0s

running (1m21.0s), 409/500 VUs, 215761 complete and 0 interrupted iterations
default   [  45% ] 409/500 VUs  1m21.0s/3m00.0s

running (1m22.0s), 419/500 VUs, 219266 complete and 0 interrupted iterations
default   [  46% ] 419/500 VUs  1m22.0s/3m00.0s

running (1m23.0s), 429/500 VUs, 222800 complete and 0 interrupted iterations
default   [  46% ] 429/500 VUs  1m23.0s/3m00.0s

running (1m24.0s), 439/500 VUs, 226173 complete and 0 interrupted iterations
default   [  47% ] 439/500 VUs  1m24.0s/3m00.0s

running (1m25.0s), 449/500 VUs, 229475 complete and 0 interrupted iterations
default   [  47% ] 449/500 VUs  1m25.0s/3m00.0s

running (1m26.0s), 459/500 VUs, 232455 complete and 0 interrupted iterations
default   [  48% ] 459/500 VUs  1m26.0s/3m00.0s

running (1m27.0s), 469/500 VUs, 235914 complete and 0 interrupted iterations
default   [  48% ] 469/500 VUs  1m27.0s/3m00.0s

running (1m28.0s), 479/500 VUs, 239350 complete and 0 interrupted iterations
default   [  49% ] 479/500 VUs  1m28.0s/3m00.0s

running (1m29.0s), 489/500 VUs, 242653 complete and 0 interrupted iterations
default   [  49% ] 489/500 VUs  1m29.0s/3m00.0s

running (1m30.0s), 499/500 VUs, 245935 complete and 0 interrupted iterations
default   [  50% ] 499/500 VUs  1m30.0s/3m00.0s

running (1m31.0s), 500/500 VUs, 249327 complete and 0 interrupted iterations
default   [  51% ] 500/500 VUs  1m31.0s/3m00.0s

running (1m32.0s), 500/500 VUs, 252793 complete and 0 interrupted iterations
default   [  51% ] 500/500 VUs  1m32.0s/3m00.0s

running (1m33.0s), 500/500 VUs, 256125 complete and 0 interrupted iterations
default   [  52% ] 500/500 VUs  1m33.0s/3m00.0s

running (1m34.0s), 500/500 VUs, 259391 complete and 0 interrupted iterations
default   [  52% ] 500/500 VUs  1m34.0s/3m00.0s

running (1m35.0s), 500/500 VUs, 262662 complete and 0 interrupted iterations
default   [  53% ] 500/500 VUs  1m35.0s/3m00.0s

running (1m36.0s), 500/500 VUs, 265877 complete and 0 interrupted iterations
default   [  53% ] 500/500 VUs  1m36.0s/3m00.0s

running (1m37.0s), 500/500 VUs, 269148 complete and 0 interrupted iterations
default   [  54% ] 500/500 VUs  1m37.0s/3m00.0s

running (1m38.0s), 500/500 VUs, 272331 complete and 0 interrupted iterations
default   [  54% ] 500/500 VUs  1m38.0s/3m00.0s

running (1m39.0s), 500/500 VUs, 275693 complete and 0 interrupted iterations
default   [  55% ] 500/500 VUs  1m39.0s/3m00.0s

running (1m40.0s), 500/500 VUs, 279046 complete and 0 interrupted iterations
default   [  56% ] 500/500 VUs  1m40.0s/3m00.0s

running (1m41.0s), 500/500 VUs, 282364 complete and 0 interrupted iterations
default   [  56% ] 500/500 VUs  1m41.0s/3m00.0s

running (1m42.0s), 500/500 VUs, 285789 complete and 0 interrupted iterations
default   [  57% ] 500/500 VUs  1m42.0s/3m00.0s

running (1m43.0s), 500/500 VUs, 289183 complete and 0 interrupted iterations
default   [  57% ] 500/500 VUs  1m43.0s/3m00.0s

running (1m44.0s), 500/500 VUs, 292789 complete and 0 interrupted iterations
default   [  58% ] 500/500 VUs  1m44.0s/3m00.0s

running (1m45.0s), 500/500 VUs, 296023 complete and 0 interrupted iterations
default   [  58% ] 500/500 VUs  1m45.0s/3m00.0s

running (1m46.0s), 500/500 VUs, 299255 complete and 0 interrupted iterations
default   [  59% ] 500/500 VUs  1m46.0s/3m00.0s

running (1m47.0s), 500/500 VUs, 302627 complete and 0 interrupted iterations
default   [  59% ] 500/500 VUs  1m47.0s/3m00.0s

running (1m48.0s), 500/500 VUs, 306050 complete and 0 interrupted iterations
default   [  60% ] 500/500 VUs  1m48.0s/3m00.0s

running (1m49.0s), 500/500 VUs, 309359 complete and 0 interrupted iterations
default   [  61% ] 500/500 VUs  1m49.0s/3m00.0s

running (1m50.0s), 500/500 VUs, 312628 complete and 0 interrupted iterations
default   [  61% ] 500/500 VUs  1m50.0s/3m00.0s

running (1m51.0s), 500/500 VUs, 316191 complete and 0 interrupted iterations
default   [  62% ] 500/500 VUs  1m51.0s/3m00.0s

running (1m52.0s), 500/500 VUs, 319624 complete and 0 interrupted iterations
default   [  62% ] 500/500 VUs  1m52.0s/3m00.0s

running (1m53.0s), 500/500 VUs, 323099 complete and 0 interrupted iterations
default   [  63% ] 500/500 VUs  1m53.0s/3m00.0s

running (1m54.0s), 500/500 VUs, 326436 complete and 0 interrupted iterations
default   [  63% ] 500/500 VUs  1m54.0s/3m00.0s

running (1m55.0s), 500/500 VUs, 329347 complete and 0 interrupted iterations
default   [  64% ] 500/500 VUs  1m55.0s/3m00.0s

running (1m56.0s), 500/500 VUs, 332423 complete and 0 interrupted iterations
default   [  64% ] 500/500 VUs  1m56.0s/3m00.0s

running (1m57.0s), 500/500 VUs, 335709 complete and 0 interrupted iterations
default   [  65% ] 500/500 VUs  1m57.0s/3m00.0s

running (1m58.0s), 500/500 VUs, 339020 complete and 0 interrupted iterations
default   [  66% ] 500/500 VUs  1m58.0s/3m00.0s

running (1m59.0s), 500/500 VUs, 342428 complete and 0 interrupted iterations
default   [  66% ] 500/500 VUs  1m59.0s/3m00.0s

running (2m00.0s), 500/500 VUs, 345563 complete and 0 interrupted iterations
default   [  67% ] 500/500 VUs  2m00.0s/3m00.0s

running (2m01.0s), 500/500 VUs, 348958 complete and 0 interrupted iterations
default   [  67% ] 500/500 VUs  2m01.0s/3m00.0s

running (2m02.0s), 500/500 VUs, 352468 complete and 0 interrupted iterations
default   [  68% ] 500/500 VUs  2m02.0s/3m00.0s

running (2m03.0s), 500/500 VUs, 355531 complete and 0 interrupted iterations
default   [  68% ] 500/500 VUs  2m03.0s/3m00.0s

running (2m04.0s), 500/500 VUs, 359027 complete and 0 interrupted iterations
default   [  69% ] 500/500 VUs  2m04.0s/3m00.0s

running (2m05.0s), 500/500 VUs, 360152 complete and 0 interrupted iterations
default   [  69% ] 500/500 VUs  2m05.0s/3m00.0s

running (2m06.0s), 500/500 VUs, 362598 complete and 0 interrupted iterations
default   [  70% ] 500/500 VUs  2m06.0s/3m00.0s

running (2m07.0s), 500/500 VUs, 364825 complete and 0 interrupted iterations
default   [  71% ] 500/500 VUs  2m07.0s/3m00.0s

running (2m08.0s), 500/500 VUs, 366816 complete and 0 interrupted iterations
default   [  71% ] 500/500 VUs  2m08.0s/3m00.0s

running (2m09.0s), 500/500 VUs, 369679 complete and 0 interrupted iterations
default   [  72% ] 500/500 VUs  2m09.0s/3m00.0s

running (2m10.0s), 500/500 VUs, 372704 complete and 0 interrupted iterations
default   [  72% ] 500/500 VUs  2m10.0s/3m00.0s

running (2m11.0s), 500/500 VUs, 375771 complete and 0 interrupted iterations
default   [  73% ] 500/500 VUs  2m11.0s/3m00.0s

running (2m12.0s), 500/500 VUs, 378744 complete and 0 interrupted iterations
default   [  73% ] 500/500 VUs  2m12.0s/3m00.0s

running (2m13.0s), 500/500 VUs, 381873 complete and 0 interrupted iterations
default   [  74% ] 500/500 VUs  2m13.0s/3m00.0s

running (2m14.0s), 500/500 VUs, 384934 complete and 0 interrupted iterations
default   [  74% ] 500/500 VUs  2m14.0s/3m00.0s

running (2m15.0s), 500/500 VUs, 388028 complete and 0 interrupted iterations
default   [  75% ] 500/500 VUs  2m15.0s/3m00.0s

running (2m16.0s), 500/500 VUs, 391001 complete and 0 interrupted iterations
default   [  76% ] 500/500 VUs  2m16.0s/3m00.0s

running (2m17.0s), 500/500 VUs, 393861 complete and 0 interrupted iterations
default   [  76% ] 500/500 VUs  2m17.0s/3m00.0s

running (2m18.0s), 500/500 VUs, 396620 complete and 0 interrupted iterations
default   [  77% ] 500/500 VUs  2m18.0s/3m00.0s

running (2m19.0s), 500/500 VUs, 399554 complete and 0 interrupted iterations
default   [  77% ] 500/500 VUs  2m19.0s/3m00.0s

running (2m20.0s), 500/500 VUs, 400889 complete and 0 interrupted iterations
default   [  78% ] 500/500 VUs  2m20.0s/3m00.0s

running (2m21.0s), 500/500 VUs, 402678 complete and 0 interrupted iterations
default   [  78% ] 500/500 VUs  2m21.0s/3m00.0s

running (2m22.0s), 500/500 VUs, 406065 complete and 0 interrupted iterations
default   [  79% ] 500/500 VUs  2m22.0s/3m00.0s

running (2m23.0s), 500/500 VUs, 409487 complete and 0 interrupted iterations
default   [  79% ] 500/500 VUs  2m23.0s/3m00.0s

running (2m24.0s), 500/500 VUs, 412661 complete and 0 interrupted iterations
default   [  80% ] 500/500 VUs  2m24.0s/3m00.0s

running (2m25.0s), 500/500 VUs, 416145 complete and 0 interrupted iterations
default   [  81% ] 500/500 VUs  2m25.0s/3m00.0s

running (2m26.0s), 500/500 VUs, 418987 complete and 0 interrupted iterations
default   [  81% ] 500/500 VUs  2m26.0s/3m00.0s

running (2m27.0s), 500/500 VUs, 422311 complete and 0 interrupted iterations
default   [  82% ] 500/500 VUs  2m27.0s/3m00.0s

running (2m28.0s), 500/500 VUs, 425872 complete and 0 interrupted iterations
default   [  82% ] 500/500 VUs  2m28.0s/3m00.0s

running (2m29.0s), 500/500 VUs, 429263 complete and 0 interrupted iterations
default   [  83% ] 500/500 VUs  2m29.0s/3m00.0s

running (2m30.0s), 500/500 VUs, 432477 complete and 0 interrupted iterations
default   [  83% ] 500/500 VUs  2m30.0s/3m00.0s

running (2m31.0s), 500/500 VUs, 435844 complete and 0 interrupted iterations
default   [  84% ] 500/500 VUs  2m31.0s/3m00.0s

running (2m32.0s), 500/500 VUs, 439376 complete and 0 interrupted iterations
default   [  84% ] 500/500 VUs  2m32.0s/3m00.0s

running (2m33.0s), 500/500 VUs, 442657 complete and 0 interrupted iterations
default   [  85% ] 500/500 VUs  2m33.0s/3m00.0s

running (2m34.0s), 500/500 VUs, 445322 complete and 0 interrupted iterations
default   [  86% ] 500/500 VUs  2m34.0s/3m00.0s

running (2m35.0s), 500/500 VUs, 446641 complete and 0 interrupted iterations
default   [  86% ] 500/500 VUs  2m35.0s/3m00.0s

running (2m36.0s), 500/500 VUs, 449531 complete and 0 interrupted iterations
default   [  87% ] 500/500 VUs  2m36.0s/3m00.0s

running (2m37.0s), 500/500 VUs, 452122 complete and 0 interrupted iterations
default   [  87% ] 500/500 VUs  2m37.0s/3m00.0s

running (2m38.0s), 500/500 VUs, 455266 complete and 0 interrupted iterations
default   [  88% ] 500/500 VUs  2m38.0s/3m00.0s

running (2m39.0s), 500/500 VUs, 458060 complete and 0 interrupted iterations
default   [  88% ] 500/500 VUs  2m39.0s/3m00.0s

running (2m40.0s), 500/500 VUs, 461276 complete and 0 interrupted iterations
default   [  89% ] 500/500 VUs  2m40.0s/3m00.0s

running (2m41.0s), 500/500 VUs, 464534 complete and 0 interrupted iterations
default   [  89% ] 500/500 VUs  2m41.0s/3m00.0s

running (2m42.0s), 500/500 VUs, 467549 complete and 0 interrupted iterations
default   [  90% ] 500/500 VUs  2m42.0s/3m00.0s

running (2m43.0s), 500/500 VUs, 470789 complete and 0 interrupted iterations
default   [  91% ] 500/500 VUs  2m43.0s/3m00.0s

running (2m44.0s), 500/500 VUs, 473823 complete and 0 interrupted iterations
default   [  91% ] 500/500 VUs  2m44.0s/3m00.0s

running (2m45.0s), 500/500 VUs, 476846 complete and 0 interrupted iterations
default   [  92% ] 500/500 VUs  2m45.0s/3m00.0s

running (2m46.0s), 500/500 VUs, 479617 complete and 0 interrupted iterations
default   [  92% ] 500/500 VUs  2m46.0s/3m00.0s

running (2m47.0s), 500/500 VUs, 482865 complete and 0 interrupted iterations
default   [  93% ] 500/500 VUs  2m47.0s/3m00.0s

running (2m48.0s), 500/500 VUs, 485904 complete and 0 interrupted iterations
default   [  93% ] 500/500 VUs  2m48.0s/3m00.0s

running (2m49.0s), 500/500 VUs, 488916 complete and 0 interrupted iterations
default   [  94% ] 500/500 VUs  2m49.0s/3m00.0s

running (2m50.0s), 500/500 VUs, 492020 complete and 0 interrupted iterations
default   [  94% ] 500/500 VUs  2m50.0s/3m00.0s

running (2m51.0s), 500/500 VUs, 495237 complete and 0 interrupted iterations
default   [  95% ] 500/500 VUs  2m51.0s/3m00.0s

running (2m52.0s), 500/500 VUs, 498384 complete and 0 interrupted iterations
default   [  96% ] 500/500 VUs  2m52.0s/3m00.0s

running (2m53.0s), 500/500 VUs, 501645 complete and 0 interrupted iterations
default   [  96% ] 500/500 VUs  2m53.0s/3m00.0s

running (2m54.0s), 500/500 VUs, 504721 complete and 0 interrupted iterations
default   [  97% ] 500/500 VUs  2m54.0s/3m00.0s

running (2m55.0s), 500/500 VUs, 507943 complete and 0 interrupted iterations
default   [  97% ] 500/500 VUs  2m55.0s/3m00.0s

running (2m56.0s), 500/500 VUs, 510595 complete and 0 interrupted iterations
default   [  98% ] 500/500 VUs  2m56.0s/3m00.0s

running (2m57.0s), 500/500 VUs, 513429 complete and 0 interrupted iterations
default   [  98% ] 500/500 VUs  2m57.0s/3m00.0s

running (2m58.0s), 500/500 VUs, 516547 complete and 0 interrupted iterations
default   [  99% ] 500/500 VUs  2m58.0s/3m00.0s

running (2m59.0s), 500/500 VUs, 519553 complete and 0 interrupted iterations
default   [  99% ] 500/500 VUs  2m59.0s/3m00.0s

running (3m00.0s), 500/500 VUs, 522664 complete and 0 interrupted iterations
default   [ 100% ] 500/500 VUs  3m00.0s/3m00.0s


  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(95)<2000' p(95)=233.48ms
    ✓ 'p(99)<5000' p(99)=319.41ms

    http_req_failed
    ✓ 'rate<0.05' rate=0.00%


  █ TOTAL RESULTS 

    checks_total.......................: 3139584 17429.958701/s
    checks_succeeded...................: 100.00% 3139584 out of 3139584
    checks_failed......................: 0.00%   0 out of 3139584

    ✓ ingest event status is 201
    ✓ ingest event has eventId in response
    ✓ ingest event has status in response
    ✓ ingest event has processedAt in response
    ✓ stress: response time < 2000ms
    ✓ stress: response has body

    HTTP
    http_req_duration.......................................................: avg=114.53ms min=1.89ms med=122.02ms max=1.02s p(90)=199.71ms p(95)=233.48ms
      { expected_response:true }............................................: avg=114.53ms min=1.89ms med=122.02ms max=1.02s p(90)=199.71ms p(95)=233.48ms
    http_req_failed.........................................................: 0.00%  0 out of 523264
    http_reqs...............................................................: 523264 2904.993117/s

    EXECUTION
    iteration_duration......................................................: avg=114.67ms min=2ms    med=122.17ms max=1.02s p(90)=199.84ms p(95)=233.63ms
    iterations..............................................................: 523264 2904.993117/s
    vus.....................................................................: 500    min=2           max=500
    vus_max.................................................................: 500    min=500         max=500

    NETWORK
    data_received...........................................................: 95 MB  528 kB/s
    data_sent...............................................................: 211 MB 1.2 MB/s




running (3m00.1s), 000/500 VUs, 523264 complete and 0 interrupted iterations
default ✓ [ 100% ] 000/500 VUs  3m0s


## Spike Test - 2025-11-13 21:29:58


         /\      Grafana   /‾‾/  
    /\  /  \     |\  __   /  /   
   /  \/    \    | |/ /  /   ‾‾\ 
  /          \   |   (  |  (‾)  |
 / __________ \  |_|\_\  \_____/ 

     execution: local
        script: k6/scripts/spike-test.js
        output: -

     scenarios: (100.00%) 1 scenario, 500 max VUs, 3m0s max duration (incl. graceful stop):
              * default: Up to 500 looping VUs for 2m30s over 7 stages (gracefulRampDown: 30s, gracefulStop: 30s)


running (0m01.0s), 003/500 VUs, 510 complete and 0 interrupted iterations
default   [   1% ] 003/500 VUs  0m01.0s/2m30.0s

running (0m02.0s), 005/500 VUs, 1801 complete and 0 interrupted iterations
default   [   1% ] 005/500 VUs  0m02.0s/2m30.0s

running (0m03.0s), 008/500 VUs, 3653 complete and 0 interrupted iterations
default   [   2% ] 008/500 VUs  0m03.0s/2m30.0s

running (0m04.0s), 010/500 VUs, 6024 complete and 0 interrupted iterations
default   [   3% ] 010/500 VUs  0m04.0s/2m30.0s

running (0m05.0s), 013/500 VUs, 7558 complete and 0 interrupted iterations
default   [   3% ] 013/500 VUs  0m05.0s/2m30.0s

running (0m06.0s), 015/500 VUs, 10191 complete and 0 interrupted iterations
default   [   4% ] 015/500 VUs  0m06.0s/2m30.0s

running (0m07.0s), 018/500 VUs, 12962 complete and 0 interrupted iterations
default   [   5% ] 018/500 VUs  0m07.0s/2m30.0s

running (0m08.0s), 020/500 VUs, 15871 complete and 0 interrupted iterations
default   [   5% ] 020/500 VUs  0m08.0s/2m30.0s

running (0m09.0s), 022/500 VUs, 18935 complete and 0 interrupted iterations
default   [   6% ] 022/500 VUs  0m09.0s/2m30.0s

running (0m10.0s), 025/500 VUs, 22061 complete and 0 interrupted iterations
default   [   7% ] 025/500 VUs  0m10.0s/2m30.0s

running (0m11.0s), 027/500 VUs, 25015 complete and 0 interrupted iterations
default   [   7% ] 027/500 VUs  0m11.0s/2m30.0s

running (0m12.0s), 030/500 VUs, 27826 complete and 0 interrupted iterations
default   [   8% ] 030/500 VUs  0m12.0s/2m30.0s

running (0m13.0s), 032/500 VUs, 30627 complete and 0 interrupted iterations
default   [   9% ] 032/500 VUs  0m13.0s/2m30.0s

running (0m14.0s), 035/500 VUs, 32977 complete and 0 interrupted iterations
default   [   9% ] 035/500 VUs  0m14.0s/2m30.0s

running (0m15.0s), 037/500 VUs, 35962 complete and 0 interrupted iterations
default   [  10% ] 037/500 VUs  0m15.0s/2m30.0s

running (0m16.0s), 040/500 VUs, 38240 complete and 0 interrupted iterations
default   [  11% ] 040/500 VUs  0m16.0s/2m30.0s

running (0m17.0s), 042/500 VUs, 39919 complete and 0 interrupted iterations
default   [  11% ] 042/500 VUs  0m17.0s/2m30.0s

running (0m18.0s), 045/500 VUs, 43185 complete and 0 interrupted iterations
default   [  12% ] 045/500 VUs  0m18.0s/2m30.0s

running (0m19.0s), 047/500 VUs, 46694 complete and 0 interrupted iterations
default   [  13% ] 047/500 VUs  0m19.0s/2m30.0s

running (0m20.0s), 049/500 VUs, 50162 complete and 0 interrupted iterations
default   [  13% ] 049/500 VUs  0m20.0s/2m30.0s

running (0m21.0s), 071/500 VUs, 53838 complete and 0 interrupted iterations
default   [  14% ] 071/500 VUs  0m21.0s/2m30.0s

running (0m22.0s), 094/500 VUs, 57178 complete and 0 interrupted iterations
default   [  15% ] 094/500 VUs  0m22.0s/2m30.0s

running (0m23.0s), 116/500 VUs, 60773 complete and 0 interrupted iterations
default   [  15% ] 116/500 VUs  0m23.0s/2m30.0s

running (0m24.0s), 139/500 VUs, 64290 complete and 0 interrupted iterations
default   [  16% ] 139/500 VUs  0m24.0s/2m30.0s

running (0m25.0s), 161/500 VUs, 67887 complete and 0 interrupted iterations
default   [  17% ] 161/500 VUs  0m25.0s/2m30.0s

running (0m26.0s), 184/500 VUs, 71631 complete and 0 interrupted iterations
default   [  17% ] 184/500 VUs  0m26.0s/2m30.0s

running (0m27.0s), 206/500 VUs, 75575 complete and 0 interrupted iterations
default   [  18% ] 206/500 VUs  0m27.0s/2m30.0s

running (0m28.0s), 229/500 VUs, 79713 complete and 0 interrupted iterations
default   [  19% ] 229/500 VUs  0m28.0s/2m30.0s

running (0m29.0s), 251/500 VUs, 83813 complete and 0 interrupted iterations
default   [  19% ] 251/500 VUs  0m29.0s/2m30.0s

running (0m30.0s), 274/500 VUs, 87903 complete and 0 interrupted iterations
default   [  20% ] 274/500 VUs  0m30.0s/2m30.0s

running (0m31.0s), 296/500 VUs, 92130 complete and 0 interrupted iterations
default   [  21% ] 296/500 VUs  0m31.0s/2m30.0s

running (0m32.0s), 319/500 VUs, 96161 complete and 0 interrupted iterations
default   [  21% ] 319/500 VUs  0m32.0s/2m30.0s

running (0m33.0s), 341/500 VUs, 100503 complete and 0 interrupted iterations
default   [  22% ] 341/500 VUs  0m33.0s/2m30.0s

running (0m34.0s), 364/500 VUs, 104951 complete and 0 interrupted iterations
default   [  23% ] 364/500 VUs  0m34.0s/2m30.0s

running (0m35.0s), 386/500 VUs, 109098 complete and 0 interrupted iterations
default   [  23% ] 386/500 VUs  0m35.0s/2m30.0s

running (0m36.0s), 409/500 VUs, 113111 complete and 0 interrupted iterations
default   [  24% ] 409/500 VUs  0m36.0s/2m30.0s

running (0m37.0s), 431/500 VUs, 117135 complete and 0 interrupted iterations
default   [  25% ] 431/500 VUs  0m37.0s/2m30.0s

running (0m38.0s), 454/500 VUs, 121618 complete and 0 interrupted iterations
default   [  25% ] 454/500 VUs  0m38.0s/2m30.0s

running (0m39.0s), 476/500 VUs, 125630 complete and 0 interrupted iterations
default   [  26% ] 476/500 VUs  0m39.0s/2m30.0s

running (0m40.0s), 499/500 VUs, 129857 complete and 0 interrupted iterations
default   [  27% ] 499/500 VUs  0m40.0s/2m30.0s

running (0m41.0s), 482/500 VUs, 133937 complete and 0 interrupted iterations
default   [  27% ] 482/500 VUs  0m41.0s/2m30.0s

running (0m42.0s), 456/500 VUs, 138109 complete and 0 interrupted iterations
default   [  28% ] 456/500 VUs  0m42.0s/2m30.0s

running (0m43.0s), 435/500 VUs, 142330 complete and 0 interrupted iterations
default   [  29% ] 435/500 VUs  0m43.0s/2m30.0s

running (0m44.0s), 412/500 VUs, 146714 complete and 0 interrupted iterations
default   [  29% ] 412/500 VUs  0m44.0s/2m30.0s

running (0m45.0s), 390/500 VUs, 150777 complete and 0 interrupted iterations
default   [  30% ] 390/500 VUs  0m45.0s/2m30.0s

running (0m46.0s), 368/500 VUs, 154932 complete and 0 interrupted iterations
default   [  31% ] 368/500 VUs  0m46.0s/2m30.0s

running (0m47.0s), 346/500 VUs, 158763 complete and 0 interrupted iterations
default   [  31% ] 346/500 VUs  0m47.0s/2m30.0s

running (0m48.0s), 323/500 VUs, 163108 complete and 0 interrupted iterations
default   [  32% ] 323/500 VUs  0m48.0s/2m30.0s

running (0m49.0s), 299/500 VUs, 167580 complete and 0 interrupted iterations
default   [  33% ] 299/500 VUs  0m49.0s/2m30.0s

running (0m50.0s), 278/500 VUs, 171696 complete and 0 interrupted iterations
default   [  33% ] 278/500 VUs  0m50.0s/2m30.0s

running (0m51.0s), 256/500 VUs, 175815 complete and 0 interrupted iterations
default   [  34% ] 256/500 VUs  0m51.0s/2m30.0s

running (0m52.0s), 232/500 VUs, 179984 complete and 0 interrupted iterations
default   [  35% ] 232/500 VUs  0m52.0s/2m30.0s

running (0m53.0s), 209/500 VUs, 184093 complete and 0 interrupted iterations
default   [  35% ] 209/500 VUs  0m53.0s/2m30.0s

running (0m54.0s), 187/500 VUs, 188408 complete and 0 interrupted iterations
default   [  36% ] 187/500 VUs  0m54.0s/2m30.0s

running (0m55.0s), 165/500 VUs, 192548 complete and 0 interrupted iterations
default   [  37% ] 165/500 VUs  0m55.0s/2m30.0s

running (0m56.0s), 144/500 VUs, 196449 complete and 0 interrupted iterations
default   [  37% ] 144/500 VUs  0m56.0s/2m30.0s

running (0m57.0s), 121/500 VUs, 200441 complete and 0 interrupted iterations
default   [  38% ] 121/500 VUs  0m57.0s/2m30.0s

running (0m58.0s), 097/500 VUs, 204650 complete and 0 interrupted iterations
default   [  39% ] 097/500 VUs  0m58.0s/2m30.0s

running (0m59.0s), 075/500 VUs, 208978 complete and 0 interrupted iterations
default   [  39% ] 075/500 VUs  0m59.0s/2m30.0s

running (1m00.0s), 052/500 VUs, 212660 complete and 0 interrupted iterations
default   [  40% ] 052/500 VUs  1m00.0s/2m30.0s

running (1m01.0s), 071/500 VUs, 216452 complete and 0 interrupted iterations
default   [  41% ] 071/500 VUs  1m01.0s/2m30.0s

running (1m02.0s), 094/500 VUs, 220160 complete and 0 interrupted iterations
default   [  41% ] 094/500 VUs  1m02.0s/2m30.0s

running (1m03.0s), 116/500 VUs, 223985 complete and 0 interrupted iterations
default   [  42% ] 116/500 VUs  1m03.0s/2m30.0s

running (1m04.0s), 139/500 VUs, 228114 complete and 0 interrupted iterations
default   [  43% ] 139/500 VUs  1m04.0s/2m30.0s

running (1m05.0s), 161/500 VUs, 232077 complete and 0 interrupted iterations
default   [  43% ] 161/500 VUs  1m05.0s/2m30.0s

running (1m06.0s), 184/500 VUs, 235980 complete and 0 interrupted iterations
default   [  44% ] 184/500 VUs  1m06.0s/2m30.0s

running (1m07.0s), 206/500 VUs, 240074 complete and 0 interrupted iterations
default   [  45% ] 206/500 VUs  1m07.0s/2m30.0s

running (1m08.0s), 229/500 VUs, 244268 complete and 0 interrupted iterations
default   [  45% ] 229/500 VUs  1m08.0s/2m30.0s

running (1m09.0s), 251/500 VUs, 248366 complete and 0 interrupted iterations
default   [  46% ] 251/500 VUs  1m09.0s/2m30.0s

running (1m10.0s), 274/500 VUs, 252666 complete and 0 interrupted iterations
default   [  47% ] 274/500 VUs  1m10.0s/2m30.0s

running (1m11.0s), 296/500 VUs, 257012 complete and 0 interrupted iterations
default   [  47% ] 296/500 VUs  1m11.0s/2m30.0s

running (1m12.0s), 319/500 VUs, 261237 complete and 0 interrupted iterations
default   [  48% ] 319/500 VUs  1m12.0s/2m30.0s

running (1m13.0s), 341/500 VUs, 265615 complete and 0 interrupted iterations
default   [  49% ] 341/500 VUs  1m13.0s/2m30.0s

running (1m14.0s), 364/500 VUs, 269813 complete and 0 interrupted iterations
default   [  49% ] 364/500 VUs  1m14.0s/2m30.0s

running (1m15.0s), 386/500 VUs, 274023 complete and 0 interrupted iterations
default   [  50% ] 386/500 VUs  1m15.0s/2m30.0s

running (1m16.0s), 409/500 VUs, 278198 complete and 0 interrupted iterations
default   [  51% ] 409/500 VUs  1m16.0s/2m30.0s

running (1m17.0s), 431/500 VUs, 282264 complete and 0 interrupted iterations
default   [  51% ] 431/500 VUs  1m17.0s/2m30.0s

running (1m18.0s), 454/500 VUs, 286398 complete and 0 interrupted iterations
default   [  52% ] 454/500 VUs  1m18.0s/2m30.0s

running (1m19.0s), 476/500 VUs, 290820 complete and 0 interrupted iterations
default   [  53% ] 476/500 VUs  1m19.0s/2m30.0s

running (1m20.0s), 499/500 VUs, 294985 complete and 0 interrupted iterations
default   [  53% ] 499/500 VUs  1m20.0s/2m30.0s

running (1m21.0s), 480/500 VUs, 299173 complete and 0 interrupted iterations
default   [  54% ] 480/500 VUs  1m21.0s/2m30.0s

running (1m22.0s), 458/500 VUs, 303391 complete and 0 interrupted iterations
default   [  55% ] 458/500 VUs  1m22.0s/2m30.0s

running (1m23.0s), 437/500 VUs, 307346 complete and 0 interrupted iterations
default   [  55% ] 437/500 VUs  1m23.0s/2m30.0s

running (1m24.0s), 412/500 VUs, 311521 complete and 0 interrupted iterations
default   [  56% ] 412/500 VUs  1m24.0s/2m30.0s

running (1m25.0s), 391/500 VUs, 315470 complete and 0 interrupted iterations
default   [  57% ] 391/500 VUs  1m25.0s/2m30.0s

running (1m26.0s), 367/500 VUs, 319741 complete and 0 interrupted iterations
default   [  57% ] 367/500 VUs  1m26.0s/2m30.0s

running (1m27.0s), 345/500 VUs, 324084 complete and 0 interrupted iterations
default   [  58% ] 345/500 VUs  1m27.0s/2m30.0s

running (1m28.0s), 322/500 VUs, 328229 complete and 0 interrupted iterations
default   [  59% ] 322/500 VUs  1m28.0s/2m30.0s

running (1m29.0s), 299/500 VUs, 332505 complete and 0 interrupted iterations
default   [  59% ] 299/500 VUs  1m29.0s/2m30.0s

running (1m30.0s), 278/500 VUs, 336539 complete and 0 interrupted iterations
default   [  60% ] 278/500 VUs  1m30.0s/2m30.0s

running (1m31.0s), 256/500 VUs, 340598 complete and 0 interrupted iterations
default   [  61% ] 256/500 VUs  1m31.0s/2m30.0s

running (1m32.0s), 232/500 VUs, 344814 complete and 0 interrupted iterations
default   [  61% ] 232/500 VUs  1m32.0s/2m30.0s

running (1m33.0s), 210/500 VUs, 348867 complete and 0 interrupted iterations
default   [  62% ] 210/500 VUs  1m33.0s/2m30.0s

running (1m34.0s), 187/500 VUs, 352916 complete and 0 interrupted iterations
default   [  63% ] 187/500 VUs  1m34.0s/2m30.0s

running (1m35.0s), 164/500 VUs, 357021 complete and 0 interrupted iterations
default   [  63% ] 164/500 VUs  1m35.0s/2m30.0s

running (1m36.0s), 142/500 VUs, 361057 complete and 0 interrupted iterations
default   [  64% ] 142/500 VUs  1m36.0s/2m30.0s

running (1m37.0s), 119/500 VUs, 365153 complete and 0 interrupted iterations
default   [  65% ] 119/500 VUs  1m37.0s/2m30.0s

running (1m38.0s), 096/500 VUs, 369288 complete and 0 interrupted iterations
default   [  65% ] 096/500 VUs  1m38.0s/2m30.0s

running (1m39.0s), 075/500 VUs, 373148 complete and 0 interrupted iterations
default   [  66% ] 075/500 VUs  1m39.0s/2m30.0s

running (1m40.0s), 052/500 VUs, 376813 complete and 0 interrupted iterations
default   [  67% ] 052/500 VUs  1m40.0s/2m30.0s

running (1m41.0s), 071/500 VUs, 380512 complete and 0 interrupted iterations
default   [  67% ] 071/500 VUs  1m41.0s/2m30.0s

running (1m42.0s), 094/500 VUs, 384620 complete and 0 interrupted iterations
default   [  68% ] 094/500 VUs  1m42.0s/2m30.0s

running (1m43.0s), 116/500 VUs, 388288 complete and 0 interrupted iterations
default   [  69% ] 116/500 VUs  1m43.0s/2m30.0s

running (1m44.0s), 139/500 VUs, 392384 complete and 0 interrupted iterations
default   [  69% ] 139/500 VUs  1m44.0s/2m30.0s

running (1m45.0s), 161/500 VUs, 395944 complete and 0 interrupted iterations
default   [  70% ] 161/500 VUs  1m45.0s/2m30.0s

running (1m46.0s), 184/500 VUs, 399596 complete and 0 interrupted iterations
default   [  71% ] 184/500 VUs  1m46.0s/2m30.0s

running (1m47.0s), 206/500 VUs, 403366 complete and 0 interrupted iterations
default   [  71% ] 206/500 VUs  1m47.0s/2m30.0s

running (1m48.0s), 229/500 VUs, 407758 complete and 0 interrupted iterations
default   [  72% ] 229/500 VUs  1m48.0s/2m30.0s

running (1m49.0s), 251/500 VUs, 412154 complete and 0 interrupted iterations
default   [  73% ] 251/500 VUs  1m49.0s/2m30.0s

running (1m50.0s), 274/500 VUs, 416432 complete and 0 interrupted iterations
default   [  73% ] 274/500 VUs  1m50.0s/2m30.0s

running (1m51.0s), 296/500 VUs, 420606 complete and 0 interrupted iterations
default   [  74% ] 296/500 VUs  1m51.0s/2m30.0s

running (1m52.0s), 319/500 VUs, 425273 complete and 0 interrupted iterations
default   [  75% ] 319/500 VUs  1m52.0s/2m30.0s

running (1m53.0s), 341/500 VUs, 429353 complete and 0 interrupted iterations
default   [  75% ] 341/500 VUs  1m53.0s/2m30.0s

running (1m54.0s), 364/500 VUs, 433302 complete and 0 interrupted iterations
default   [  76% ] 364/500 VUs  1m54.0s/2m30.0s

running (1m55.0s), 386/500 VUs, 437470 complete and 0 interrupted iterations
default   [  77% ] 386/500 VUs  1m55.0s/2m30.0s

running (1m56.0s), 409/500 VUs, 441763 complete and 0 interrupted iterations
default   [  77% ] 409/500 VUs  1m56.0s/2m30.0s

running (1m57.0s), 431/500 VUs, 445975 complete and 0 interrupted iterations
default   [  78% ] 431/500 VUs  1m57.0s/2m30.0s

running (1m58.0s), 454/500 VUs, 450243 complete and 0 interrupted iterations
default   [  79% ] 454/500 VUs  1m58.0s/2m30.0s

running (1m59.0s), 476/500 VUs, 454524 complete and 0 interrupted iterations
default   [  79% ] 476/500 VUs  1m59.0s/2m30.0s

running (2m00.0s), 499/500 VUs, 458540 complete and 0 interrupted iterations
default   [  80% ] 499/500 VUs  2m00.0s/2m30.0s

running (2m01.0s), 486/500 VUs, 462714 complete and 0 interrupted iterations
default   [  81% ] 486/500 VUs  2m01.0s/2m30.0s

running (2m02.0s), 472/500 VUs, 466841 complete and 0 interrupted iterations
default   [  81% ] 472/500 VUs  2m02.0s/2m30.0s

running (2m03.0s), 457/500 VUs, 471219 complete and 0 interrupted iterations
default   [  82% ] 457/500 VUs  2m03.0s/2m30.0s

running (2m04.0s), 442/500 VUs, 475376 complete and 0 interrupted iterations
default   [  83% ] 442/500 VUs  2m04.0s/2m30.0s

running (2m05.0s), 427/500 VUs, 479214 complete and 0 interrupted iterations
default   [  83% ] 427/500 VUs  2m05.0s/2m30.0s

running (2m06.0s), 414/500 VUs, 483281 complete and 0 interrupted iterations
default   [  84% ] 414/500 VUs  2m06.0s/2m30.0s

running (2m07.0s), 396/500 VUs, 487594 complete and 0 interrupted iterations
default   [  85% ] 396/500 VUs  2m07.0s/2m30.0s

running (2m08.0s), 381/500 VUs, 491670 complete and 0 interrupted iterations
default   [  85% ] 381/500 VUs  2m08.0s/2m30.0s

running (2m09.0s), 368/500 VUs, 495830 complete and 0 interrupted iterations
default   [  86% ] 368/500 VUs  2m09.0s/2m30.0s

running (2m10.0s), 352/500 VUs, 499848 complete and 0 interrupted iterations
default   [  87% ] 352/500 VUs  2m10.0s/2m30.0s

running (2m11.0s), 337/500 VUs, 502141 complete and 0 interrupted iterations
default   [  87% ] 337/500 VUs  2m11.0s/2m30.0s

running (2m12.0s), 322/500 VUs, 505702 complete and 0 interrupted iterations
default   [  88% ] 322/500 VUs  2m12.0s/2m30.0s

running (2m13.0s), 306/500 VUs, 509763 complete and 0 interrupted iterations
default   [  89% ] 306/500 VUs  2m13.0s/2m30.0s

running (2m14.0s), 292/500 VUs, 513562 complete and 0 interrupted iterations
default   [  89% ] 292/500 VUs  2m14.0s/2m30.0s

running (2m15.0s), 277/500 VUs, 517520 complete and 0 interrupted iterations
default   [  90% ] 277/500 VUs  2m15.0s/2m30.0s

running (2m16.0s), 263/500 VUs, 521005 complete and 0 interrupted iterations
default   [  91% ] 263/500 VUs  2m16.0s/2m30.0s

running (2m17.0s), 247/500 VUs, 525204 complete and 0 interrupted iterations
default   [  91% ] 247/500 VUs  2m17.0s/2m30.0s

running (2m18.0s), 232/500 VUs, 529436 complete and 0 interrupted iterations
default   [  92% ] 232/500 VUs  2m18.0s/2m30.0s

running (2m19.0s), 217/500 VUs, 533641 complete and 0 interrupted iterations
default   [  93% ] 217/500 VUs  2m19.0s/2m30.0s

running (2m20.0s), 201/500 VUs, 537694 complete and 0 interrupted iterations
default   [  93% ] 201/500 VUs  2m20.0s/2m30.0s

running (2m21.0s), 187/500 VUs, 541651 complete and 0 interrupted iterations
default   [  94% ] 187/500 VUs  2m21.0s/2m30.0s

running (2m22.0s), 171/500 VUs, 545790 complete and 0 interrupted iterations
default   [  95% ] 171/500 VUs  2m22.0s/2m30.0s

running (2m23.0s), 156/500 VUs, 549908 complete and 0 interrupted iterations
default   [  95% ] 156/500 VUs  2m23.0s/2m30.0s

running (2m24.0s), 141/500 VUs, 553975 complete and 0 interrupted iterations
default   [  96% ] 141/500 VUs  2m24.0s/2m30.0s

running (2m25.0s), 126/500 VUs, 557876 complete and 0 interrupted iterations
default   [  97% ] 126/500 VUs  2m25.0s/2m30.0s

running (2m26.0s), 111/500 VUs, 561921 complete and 0 interrupted iterations
default   [  97% ] 111/500 VUs  2m26.0s/2m30.0s

running (2m27.0s), 096/500 VUs, 566351 complete and 0 interrupted iterations
default   [  98% ] 096/500 VUs  2m27.0s/2m30.0s

running (2m28.0s), 081/500 VUs, 570864 complete and 0 interrupted iterations
default   [  99% ] 081/500 VUs  2m28.0s/2m30.0s

running (2m29.0s), 066/500 VUs, 574779 complete and 0 interrupted iterations
default   [  99% ] 066/500 VUs  2m29.0s/2m30.0s

running (2m30.0s), 051/500 VUs, 577810 complete and 0 interrupted iterations
default   [ 100% ] 051/500 VUs  2m30.0s/2m30.0s


  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(95)<2000' p(95)=140.55ms

    http_req_failed
    ✓ 'rate<0.02' rate=0.00%


  █ TOTAL RESULTS 

    checks_total.......................: 3467388 23106.165963/s
    checks_succeeded...................: 100.00% 3467388 out of 3467388
    checks_failed......................: 0.00%   0 out of 3467388

    ✓ ingest event status is 201
    ✓ ingest event has eventId in response
    ✓ ingest event has status in response
    ✓ ingest event has processedAt in response
    ✓ spike: response time < 2000ms
    ✓ spike: response has body

    HTTP
    http_req_duration.......................................................: avg=62.64ms min=1.78ms med=57.25ms max=523.32ms p(90)=120ms    p(95)=140.55ms
      { expected_response:true }............................................: avg=62.64ms min=1.78ms med=57.25ms max=523.32ms p(90)=120ms    p(95)=140.55ms
    http_req_failed.........................................................: 0.00%  0 out of 577898
    http_reqs...............................................................: 577898 3851.027661/s

    EXECUTION
    iteration_duration......................................................: avg=62.83ms min=1.9ms  med=57.45ms max=523.46ms p(90)=120.18ms p(95)=140.79ms
    iterations..............................................................: 577898 3851.027661/s
    vus.....................................................................: 51     min=3           max=499
    vus_max.................................................................: 500    min=500         max=500

    NETWORK
    data_received...........................................................: 105 MB 700 kB/s
    data_sent...............................................................: 233 MB 1.6 MB/s




running (2m30.1s), 000/500 VUs, 577898 complete and 0 interrupted iterations
default ✓ [ 100% ] 000/500 VUs  2m30s

