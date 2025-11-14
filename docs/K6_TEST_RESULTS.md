# k6 Test Results

This file contains the latest results from k6 performance tests. Results are automatically updated after each test run.

**Related Documentation:**
- **[README](../README.md)** - Project overview and quick start
- **[K6 Performance Testing](K6_PERFORMANCE.md)** - Comprehensive k6 testing guide
- **[Development Guide](DEVELOPMENT.md)** - Complete development guide with all make commands

## Warm-up Test - 2025-11-13 22:14:57


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


running (01.0s), 1/2 VUs, 305 complete and 0 interrupted iterations
default   [  10% ] 1/2 VUs  01.0s/10.0s

running (02.0s), 1/2 VUs, 558 complete and 0 interrupted iterations
default   [  20% ] 1/2 VUs  02.0s/10.0s

running (03.0s), 1/2 VUs, 870 complete and 0 interrupted iterations
default   [  30% ] 1/2 VUs  03.0s/10.0s

running (04.0s), 1/2 VUs, 1088 complete and 0 interrupted iterations
default   [  40% ] 1/2 VUs  04.0s/10.0s

running (05.0s), 1/2 VUs, 1325 complete and 0 interrupted iterations
default   [  50% ] 1/2 VUs  05.0s/10.0s

running (06.0s), 2/2 VUs, 1750 complete and 0 interrupted iterations
default   [  60% ] 2/2 VUs  06.0s/10.0s

running (07.0s), 2/2 VUs, 2431 complete and 0 interrupted iterations
default   [  70% ] 2/2 VUs  07.0s/10.0s

running (08.0s), 2/2 VUs, 3100 complete and 0 interrupted iterations
default   [  80% ] 2/2 VUs  08.0s/10.0s

running (09.0s), 2/2 VUs, 3775 complete and 0 interrupted iterations
default   [  90% ] 2/2 VUs  09.0s/10.0s

running (10.0s), 2/2 VUs, 4267 complete and 0 interrupted iterations
default   [ 100% ] 2/2 VUs  10.0s/10.0s


  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(95)<500' p(95)=5.57ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.00%


  █ TOTAL RESULTS 

    checks_total.......................: 25614   2560.33183/s
    checks_succeeded...................: 100.00% 25614 out of 25614
    checks_failed......................: 0.00%   0 out of 25614

    ✓ ingest event status is 201
    ✓ ingest event has eventId in response
    ✓ ingest event has status in response
    ✓ ingest event has processedAt in response
    ✓ warmup: response time < 500ms
    ✓ warmup: response has body

    HTTP
    http_req_duration.......................................................: avg=3.35ms min=1.83ms med=2.85ms max=38.45ms p(90)=4.78ms p(95)=5.57ms
      { expected_response:true }............................................: avg=3.35ms min=1.83ms med=2.85ms max=38.45ms p(90)=4.78ms p(95)=5.57ms
    http_req_failed.........................................................: 0.00%  0 out of 4269
    http_reqs...............................................................: 4269   426.721972/s

    EXECUTION
    iteration_duration......................................................: avg=3.5ms  min=1.91ms med=2.97ms max=40.85ms p(90)=5.03ms p(95)=5.83ms
    iterations..............................................................: 4269   426.721972/s
    vus.....................................................................: 2      min=1         max=2
    vus_max.................................................................: 2      min=2         max=2

    NETWORK
    data_received...........................................................: 777 kB 78 kB/s
    data_sent...............................................................: 1.7 MB 172 kB/s




running (10.0s), 0/2 VUs, 4269 complete and 0 interrupted iterations
default ✓ [ 100% ] 0/2 VUs  10s


## Smoke Test - 2025-11-13 22:16:23


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

running (0m02.0s), 01/10 VUs, 597 complete and 0 interrupted iterations
default   [   3% ] 01/10 VUs  0m02.0s/1m00.0s

running (0m03.0s), 01/10 VUs, 997 complete and 0 interrupted iterations
default   [   5% ] 01/10 VUs  0m03.0s/1m00.0s

running (0m04.0s), 02/10 VUs, 1532 complete and 0 interrupted iterations
default   [   7% ] 02/10 VUs  0m04.0s/1m00.0s

running (0m05.0s), 02/10 VUs, 2170 complete and 0 interrupted iterations
default   [   8% ] 02/10 VUs  0m05.0s/1m00.0s

running (0m06.0s), 02/10 VUs, 2808 complete and 0 interrupted iterations
default   [  10% ] 02/10 VUs  0m06.0s/1m00.0s

running (0m07.0s), 03/10 VUs, 3593 complete and 0 interrupted iterations
default   [  12% ] 03/10 VUs  0m07.0s/1m00.0s

running (0m08.0s), 03/10 VUs, 4624 complete and 0 interrupted iterations
default   [  13% ] 03/10 VUs  0m08.0s/1m00.0s

running (0m09.0s), 03/10 VUs, 5632 complete and 0 interrupted iterations
default   [  15% ] 03/10 VUs  0m09.0s/1m00.0s

running (0m10.0s), 03/10 VUs, 6526 complete and 0 interrupted iterations
default   [  17% ] 03/10 VUs  0m10.0s/1m00.0s

running (0m11.0s), 04/10 VUs, 7275 complete and 0 interrupted iterations
default   [  18% ] 04/10 VUs  0m11.0s/1m00.0s

running (0m12.0s), 04/10 VUs, 8499 complete and 0 interrupted iterations
default   [  20% ] 04/10 VUs  0m12.0s/1m00.0s

running (0m13.0s), 04/10 VUs, 9755 complete and 0 interrupted iterations
default   [  22% ] 04/10 VUs  0m13.0s/1m00.0s

running (0m14.0s), 05/10 VUs, 11294 complete and 0 interrupted iterations
default   [  23% ] 05/10 VUs  0m14.0s/1m00.0s

running (0m15.0s), 05/10 VUs, 12969 complete and 0 interrupted iterations
default   [  25% ] 05/10 VUs  0m15.0s/1m00.0s

running (0m16.0s), 05/10 VUs, 14423 complete and 0 interrupted iterations
default   [  27% ] 05/10 VUs  0m16.0s/1m00.0s

running (0m17.0s), 06/10 VUs, 16040 complete and 0 interrupted iterations
default   [  28% ] 06/10 VUs  0m17.0s/1m00.0s

running (0m18.0s), 06/10 VUs, 17921 complete and 0 interrupted iterations
default   [  30% ] 06/10 VUs  0m18.0s/1m00.0s

running (0m19.0s), 06/10 VUs, 19812 complete and 0 interrupted iterations
default   [  32% ] 06/10 VUs  0m19.0s/1m00.0s

running (0m20.0s), 06/10 VUs, 21657 complete and 0 interrupted iterations
default   [  33% ] 06/10 VUs  0m20.0s/1m00.0s

running (0m21.0s), 07/10 VUs, 23738 complete and 0 interrupted iterations
default   [  35% ] 07/10 VUs  0m21.0s/1m00.0s

running (0m22.0s), 07/10 VUs, 25750 complete and 0 interrupted iterations
default   [  37% ] 07/10 VUs  0m22.0s/1m00.0s

running (0m23.0s), 07/10 VUs, 27829 complete and 0 interrupted iterations
default   [  38% ] 07/10 VUs  0m23.0s/1m00.0s

running (0m24.0s), 08/10 VUs, 30035 complete and 0 interrupted iterations
default   [  40% ] 08/10 VUs  0m24.0s/1m00.0s

running (0m25.0s), 08/10 VUs, 32366 complete and 0 interrupted iterations
default   [  42% ] 08/10 VUs  0m25.0s/1m00.0s

running (0m26.0s), 08/10 VUs, 34643 complete and 0 interrupted iterations
default   [  43% ] 08/10 VUs  0m26.0s/1m00.0s

running (0m27.0s), 09/10 VUs, 36872 complete and 0 interrupted iterations
default   [  45% ] 09/10 VUs  0m27.0s/1m00.0s

running (0m28.0s), 09/10 VUs, 39306 complete and 0 interrupted iterations
default   [  47% ] 09/10 VUs  0m28.0s/1m00.0s

running (0m29.0s), 09/10 VUs, 41416 complete and 0 interrupted iterations
default   [  48% ] 09/10 VUs  0m29.0s/1m00.0s

running (0m30.0s), 09/10 VUs, 43437 complete and 0 interrupted iterations
default   [  50% ] 09/10 VUs  0m30.0s/1m00.0s

running (0m31.0s), 10/10 VUs, 45959 complete and 0 interrupted iterations
default   [  52% ] 10/10 VUs  0m31.0s/1m00.0s

running (0m32.0s), 10/10 VUs, 48467 complete and 0 interrupted iterations
default   [  53% ] 10/10 VUs  0m32.0s/1m00.0s

running (0m33.0s), 10/10 VUs, 50918 complete and 0 interrupted iterations
default   [  55% ] 10/10 VUs  0m33.0s/1m00.0s

running (0m34.0s), 10/10 VUs, 53444 complete and 0 interrupted iterations
default   [  57% ] 10/10 VUs  0m34.0s/1m00.0s

running (0m35.0s), 10/10 VUs, 55985 complete and 0 interrupted iterations
default   [  58% ] 10/10 VUs  0m35.0s/1m00.0s

running (0m36.0s), 10/10 VUs, 58412 complete and 0 interrupted iterations
default   [  60% ] 10/10 VUs  0m36.0s/1m00.0s

running (0m37.0s), 10/10 VUs, 60850 complete and 0 interrupted iterations
default   [  62% ] 10/10 VUs  0m37.0s/1m00.0s

running (0m38.0s), 10/10 VUs, 63393 complete and 0 interrupted iterations
default   [  63% ] 10/10 VUs  0m38.0s/1m00.0s

running (0m39.0s), 10/10 VUs, 66001 complete and 0 interrupted iterations
default   [  65% ] 10/10 VUs  0m39.0s/1m00.0s

running (0m40.0s), 10/10 VUs, 68524 complete and 0 interrupted iterations
default   [  67% ] 10/10 VUs  0m40.0s/1m00.0s

running (0m41.0s), 10/10 VUs, 71101 complete and 0 interrupted iterations
default   [  68% ] 10/10 VUs  0m41.0s/1m00.0s

running (0m42.0s), 10/10 VUs, 73645 complete and 0 interrupted iterations
default   [  70% ] 10/10 VUs  0m42.0s/1m00.0s

running (0m43.0s), 10/10 VUs, 76068 complete and 0 interrupted iterations
default   [  72% ] 10/10 VUs  0m43.0s/1m00.0s

running (0m44.0s), 10/10 VUs, 78644 complete and 0 interrupted iterations
default   [  73% ] 10/10 VUs  0m44.0s/1m00.0s

running (0m45.0s), 10/10 VUs, 81194 complete and 0 interrupted iterations
default   [  75% ] 10/10 VUs  0m45.0s/1m00.0s

running (0m46.0s), 10/10 VUs, 83612 complete and 0 interrupted iterations
default   [  77% ] 10/10 VUs  0m46.0s/1m00.0s

running (0m47.0s), 10/10 VUs, 86103 complete and 0 interrupted iterations
default   [  78% ] 10/10 VUs  0m47.0s/1m00.0s

running (0m48.0s), 10/10 VUs, 88513 complete and 0 interrupted iterations
default   [  80% ] 10/10 VUs  0m48.0s/1m00.0s

running (0m49.0s), 10/10 VUs, 91000 complete and 0 interrupted iterations
default   [  82% ] 10/10 VUs  0m49.0s/1m00.0s

running (0m50.0s), 10/10 VUs, 93212 complete and 0 interrupted iterations
default   [  83% ] 10/10 VUs  0m50.0s/1m00.0s

running (0m51.0s), 10/10 VUs, 95463 complete and 0 interrupted iterations
default   [  85% ] 10/10 VUs  0m51.0s/1m00.0s

running (0m52.0s), 10/10 VUs, 97730 complete and 0 interrupted iterations
default   [  87% ] 10/10 VUs  0m52.0s/1m00.0s

running (0m53.0s), 10/10 VUs, 99881 complete and 0 interrupted iterations
default   [  88% ] 10/10 VUs  0m53.0s/1m00.0s

running (0m54.0s), 10/10 VUs, 102480 complete and 0 interrupted iterations
default   [  90% ] 10/10 VUs  0m54.0s/1m00.0s

running (0m55.0s), 10/10 VUs, 104973 complete and 0 interrupted iterations
default   [  92% ] 10/10 VUs  0m55.0s/1m00.0s

running (0m56.0s), 10/10 VUs, 107544 complete and 0 interrupted iterations
default   [  93% ] 10/10 VUs  0m56.0s/1m00.0s

running (0m57.0s), 10/10 VUs, 110007 complete and 0 interrupted iterations
default   [  95% ] 10/10 VUs  0m57.0s/1m00.0s

running (0m58.0s), 10/10 VUs, 112648 complete and 0 interrupted iterations
default   [  97% ] 10/10 VUs  0m58.0s/1m00.0s

running (0m59.0s), 10/10 VUs, 114740 complete and 0 interrupted iterations
default   [  98% ] 10/10 VUs  0m59.0s/1m00.0s

running (1m00.0s), 10/10 VUs, 117173 complete and 0 interrupted iterations
default   [ 100% ] 10/10 VUs  1m00.0s/1m00.0s


  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(95)<200' p(95)=5.42ms
    ✓ 'p(99)<500' p(99)=8.68ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.00%


  █ TOTAL RESULTS 

    checks_total.......................: 703212  11719.598785/s
    checks_succeeded...................: 100.00% 703212 out of 703212
    checks_failed......................: 0.00%   0 out of 703212

    ✓ ingest event status is 201
    ✓ ingest event has eventId in response
    ✓ ingest event has status in response
    ✓ ingest event has processedAt in response
    ✓ smoke: response time < 200ms
    ✓ smoke: response has body

    HTTP
    http_req_duration.......................................................: avg=3.71ms min=1.72ms med=3.47ms max=170.8ms  p(90)=4.69ms p(95)=5.42ms
      { expected_response:true }............................................: avg=3.71ms min=1.72ms med=3.47ms max=170.8ms  p(90)=4.69ms p(95)=5.42ms
    http_req_failed.........................................................: 0.00%  0 out of 117202
    http_reqs...............................................................: 117202 1953.266464/s

    EXECUTION
    iteration_duration......................................................: avg=3.83ms min=1.8ms  med=3.59ms max=171.96ms p(90)=4.82ms p(95)=5.57ms
    iterations..............................................................: 117202 1953.266464/s
    vus.....................................................................: 10     min=1           max=10
    vus_max.................................................................: 10     min=10          max=10

    NETWORK
    data_received...........................................................: 21 MB  355 kB/s
    data_sent...............................................................: 47 MB  787 kB/s




running (1m00.0s), 00/10 VUs, 117202 complete and 0 interrupted iterations
default ✓ [ 100% ] 00/10 VUs  1m0s


## Load Test - 2025-11-13 22:18:48


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


running (0m01.0s), 012/350 VUs, 1066 complete and 0 interrupted iterations
default   [   1% ] 012/350 VUs  0m01.0s/2m00.0s

running (0m02.0s), 023/350 VUs, 2984 complete and 0 interrupted iterations
default   [   2% ] 023/350 VUs  0m02.0s/2m00.0s

running (0m03.0s), 035/350 VUs, 5263 complete and 0 interrupted iterations
default   [   2% ] 035/350 VUs  0m03.0s/2m00.0s

running (0m04.0s), 047/350 VUs, 7495 complete and 0 interrupted iterations
default   [   3% ] 047/350 VUs  0m04.0s/2m00.0s

running (0m05.0s), 058/350 VUs, 10043 complete and 0 interrupted iterations
default   [   4% ] 058/350 VUs  0m05.0s/2m00.0s

running (0m06.0s), 070/350 VUs, 12493 complete and 0 interrupted iterations
default   [   5% ] 070/350 VUs  0m06.0s/2m00.0s

running (0m07.0s), 082/350 VUs, 14973 complete and 0 interrupted iterations
default   [   6% ] 082/350 VUs  0m07.0s/2m00.0s

running (0m08.0s), 093/350 VUs, 17558 complete and 0 interrupted iterations
default   [   7% ] 093/350 VUs  0m08.0s/2m00.0s

running (0m09.0s), 105/350 VUs, 20039 complete and 0 interrupted iterations
default   [   7% ] 105/350 VUs  0m09.0s/2m00.0s

running (0m10.0s), 116/350 VUs, 22852 complete and 0 interrupted iterations
default   [   8% ] 116/350 VUs  0m10.0s/2m00.0s

running (0m11.0s), 128/350 VUs, 25672 complete and 0 interrupted iterations
default   [   9% ] 128/350 VUs  0m11.0s/2m00.0s

running (0m12.0s), 140/350 VUs, 28264 complete and 0 interrupted iterations
default   [  10% ] 140/350 VUs  0m12.0s/2m00.0s

running (0m13.0s), 151/350 VUs, 31073 complete and 0 interrupted iterations
default   [  11% ] 151/350 VUs  0m13.0s/2m00.0s

running (0m14.0s), 163/350 VUs, 33979 complete and 0 interrupted iterations
default   [  12% ] 163/350 VUs  0m14.0s/2m00.0s

running (0m15.0s), 175/350 VUs, 36685 complete and 0 interrupted iterations
default   [  12% ] 175/350 VUs  0m15.0s/2m00.0s

running (0m16.0s), 186/350 VUs, 38355 complete and 0 interrupted iterations
default   [  13% ] 186/350 VUs  0m16.0s/2m00.0s

running (0m17.0s), 198/350 VUs, 39132 complete and 0 interrupted iterations
default   [  14% ] 198/350 VUs  0m17.0s/2m00.0s

running (0m18.0s), 210/350 VUs, 40684 complete and 0 interrupted iterations
default   [  15% ] 210/350 VUs  0m18.0s/2m00.0s

running (0m19.0s), 221/350 VUs, 43058 complete and 0 interrupted iterations
default   [  16% ] 221/350 VUs  0m19.0s/2m00.0s

running (0m20.0s), 233/350 VUs, 45693 complete and 0 interrupted iterations
default   [  17% ] 233/350 VUs  0m20.0s/2m00.0s

running (0m21.0s), 244/350 VUs, 48737 complete and 0 interrupted iterations
default   [  17% ] 244/350 VUs  0m21.0s/2m00.0s

running (0m22.0s), 256/350 VUs, 51780 complete and 0 interrupted iterations
default   [  18% ] 256/350 VUs  0m22.0s/2m00.0s

running (0m23.0s), 268/350 VUs, 54747 complete and 0 interrupted iterations
default   [  19% ] 268/350 VUs  0m23.0s/2m00.0s

running (0m24.0s), 279/350 VUs, 57577 complete and 0 interrupted iterations
default   [  20% ] 279/350 VUs  0m24.0s/2m00.0s

running (0m25.0s), 291/350 VUs, 60437 complete and 0 interrupted iterations
default   [  21% ] 291/350 VUs  0m25.0s/2m00.0s

running (0m26.0s), 303/350 VUs, 63524 complete and 0 interrupted iterations
default   [  22% ] 303/350 VUs  0m26.0s/2m00.0s

running (0m27.0s), 314/350 VUs, 65983 complete and 0 interrupted iterations
default   [  22% ] 314/350 VUs  0m27.0s/2m00.0s

running (0m28.0s), 326/350 VUs, 68831 complete and 0 interrupted iterations
default   [  23% ] 326/350 VUs  0m28.0s/2m00.0s

running (0m29.0s), 337/350 VUs, 71135 complete and 0 interrupted iterations
default   [  24% ] 337/350 VUs  0m29.0s/2m00.0s

running (0m30.0s), 349/350 VUs, 74309 complete and 0 interrupted iterations
default   [  25% ] 349/350 VUs  0m30.0s/2m00.0s

running (0m31.0s), 350/350 VUs, 77088 complete and 0 interrupted iterations
default   [  26% ] 350/350 VUs  0m31.0s/2m00.0s

running (0m32.0s), 350/350 VUs, 79755 complete and 0 interrupted iterations
default   [  27% ] 350/350 VUs  0m32.0s/2m00.0s

running (0m33.0s), 350/350 VUs, 82982 complete and 0 interrupted iterations
default   [  27% ] 350/350 VUs  0m33.0s/2m00.0s

running (0m34.0s), 350/350 VUs, 85936 complete and 0 interrupted iterations
default   [  28% ] 350/350 VUs  0m34.0s/2m00.0s

running (0m35.0s), 350/350 VUs, 88621 complete and 0 interrupted iterations
default   [  29% ] 350/350 VUs  0m35.0s/2m00.0s

running (0m36.0s), 350/350 VUs, 91559 complete and 0 interrupted iterations
default   [  30% ] 350/350 VUs  0m36.0s/2m00.0s

running (0m37.0s), 350/350 VUs, 94225 complete and 0 interrupted iterations
default   [  31% ] 350/350 VUs  0m37.0s/2m00.0s

running (0m38.0s), 350/350 VUs, 97293 complete and 0 interrupted iterations
default   [  32% ] 350/350 VUs  0m38.0s/2m00.0s

running (0m39.0s), 350/350 VUs, 99969 complete and 0 interrupted iterations
default   [  32% ] 350/350 VUs  0m39.0s/2m00.0s

running (0m40.0s), 350/350 VUs, 103172 complete and 0 interrupted iterations
default   [  33% ] 350/350 VUs  0m40.0s/2m00.0s

running (0m41.0s), 350/350 VUs, 106168 complete and 0 interrupted iterations
default   [  34% ] 350/350 VUs  0m41.0s/2m00.0s

running (0m42.0s), 350/350 VUs, 109326 complete and 0 interrupted iterations
default   [  35% ] 350/350 VUs  0m42.0s/2m00.0s

running (0m43.0s), 350/350 VUs, 112439 complete and 0 interrupted iterations
default   [  36% ] 350/350 VUs  0m43.0s/2m00.0s

running (0m44.0s), 350/350 VUs, 115596 complete and 0 interrupted iterations
default   [  37% ] 350/350 VUs  0m44.0s/2m00.0s

running (0m45.0s), 350/350 VUs, 118426 complete and 0 interrupted iterations
default   [  37% ] 350/350 VUs  0m45.0s/2m00.0s

running (0m46.0s), 350/350 VUs, 121452 complete and 0 interrupted iterations
default   [  38% ] 350/350 VUs  0m46.0s/2m00.0s

running (0m47.0s), 350/350 VUs, 124509 complete and 0 interrupted iterations
default   [  39% ] 350/350 VUs  0m47.0s/2m00.0s

running (0m48.0s), 350/350 VUs, 127382 complete and 0 interrupted iterations
default   [  40% ] 350/350 VUs  0m48.0s/2m00.0s

running (0m49.0s), 350/350 VUs, 130389 complete and 0 interrupted iterations
default   [  41% ] 350/350 VUs  0m49.0s/2m00.0s

running (0m50.0s), 350/350 VUs, 132757 complete and 0 interrupted iterations
default   [  42% ] 350/350 VUs  0m50.0s/2m00.0s

running (0m51.0s), 350/350 VUs, 135772 complete and 0 interrupted iterations
default   [  42% ] 350/350 VUs  0m51.0s/2m00.0s

running (0m52.0s), 350/350 VUs, 139016 complete and 0 interrupted iterations
default   [  43% ] 350/350 VUs  0m52.0s/2m00.0s

running (0m53.0s), 350/350 VUs, 141828 complete and 0 interrupted iterations
default   [  44% ] 350/350 VUs  0m53.0s/2m00.0s

running (0m54.0s), 350/350 VUs, 144933 complete and 0 interrupted iterations
default   [  45% ] 350/350 VUs  0m54.0s/2m00.0s

running (0m55.0s), 350/350 VUs, 148012 complete and 0 interrupted iterations
default   [  46% ] 350/350 VUs  0m55.0s/2m00.0s

running (0m56.0s), 350/350 VUs, 150672 complete and 0 interrupted iterations
default   [  47% ] 350/350 VUs  0m56.0s/2m00.0s

running (0m57.0s), 350/350 VUs, 153705 complete and 0 interrupted iterations
default   [  47% ] 350/350 VUs  0m57.0s/2m00.0s

running (0m58.0s), 350/350 VUs, 156797 complete and 0 interrupted iterations
default   [  48% ] 350/350 VUs  0m58.0s/2m00.0s

running (0m59.0s), 350/350 VUs, 159998 complete and 0 interrupted iterations
default   [  49% ] 350/350 VUs  0m59.0s/2m00.0s

running (1m00.0s), 350/350 VUs, 163110 complete and 0 interrupted iterations
default   [  50% ] 350/350 VUs  1m00.0s/2m00.0s

running (1m01.0s), 350/350 VUs, 165867 complete and 0 interrupted iterations
default   [  51% ] 350/350 VUs  1m01.0s/2m00.0s

running (1m02.0s), 350/350 VUs, 169011 complete and 0 interrupted iterations
default   [  52% ] 350/350 VUs  1m02.0s/2m00.0s

running (1m03.0s), 350/350 VUs, 171836 complete and 0 interrupted iterations
default   [  52% ] 350/350 VUs  1m03.0s/2m00.0s

running (1m04.0s), 350/350 VUs, 174849 complete and 0 interrupted iterations
default   [  53% ] 350/350 VUs  1m04.0s/2m00.0s

running (1m05.0s), 350/350 VUs, 177814 complete and 0 interrupted iterations
default   [  54% ] 350/350 VUs  1m05.0s/2m00.0s

running (1m06.0s), 350/350 VUs, 181211 complete and 0 interrupted iterations
default   [  55% ] 350/350 VUs  1m06.0s/2m00.0s

running (1m07.0s), 350/350 VUs, 184272 complete and 0 interrupted iterations
default   [  56% ] 350/350 VUs  1m07.0s/2m00.0s

running (1m08.0s), 350/350 VUs, 187351 complete and 0 interrupted iterations
default   [  57% ] 350/350 VUs  1m08.0s/2m00.0s

running (1m09.0s), 350/350 VUs, 190607 complete and 0 interrupted iterations
default   [  57% ] 350/350 VUs  1m09.0s/2m00.0s

running (1m10.0s), 350/350 VUs, 193694 complete and 0 interrupted iterations
default   [  58% ] 350/350 VUs  1m10.0s/2m00.0s

running (1m11.0s), 350/350 VUs, 196756 complete and 0 interrupted iterations
default   [  59% ] 350/350 VUs  1m11.0s/2m00.0s

running (1m12.0s), 350/350 VUs, 199989 complete and 0 interrupted iterations
default   [  60% ] 350/350 VUs  1m12.0s/2m00.0s

running (1m13.0s), 350/350 VUs, 203094 complete and 0 interrupted iterations
default   [  61% ] 350/350 VUs  1m13.0s/2m00.0s

running (1m14.0s), 350/350 VUs, 206249 complete and 0 interrupted iterations
default   [  62% ] 350/350 VUs  1m14.0s/2m00.0s

running (1m15.0s), 350/350 VUs, 209382 complete and 0 interrupted iterations
default   [  62% ] 350/350 VUs  1m15.0s/2m00.0s

running (1m16.0s), 350/350 VUs, 212600 complete and 0 interrupted iterations
default   [  63% ] 350/350 VUs  1m16.0s/2m00.0s

running (1m17.0s), 350/350 VUs, 215704 complete and 0 interrupted iterations
default   [  64% ] 350/350 VUs  1m17.0s/2m00.0s

running (1m18.0s), 350/350 VUs, 218825 complete and 0 interrupted iterations
default   [  65% ] 350/350 VUs  1m18.0s/2m00.0s

running (1m19.0s), 350/350 VUs, 222047 complete and 0 interrupted iterations
default   [  66% ] 350/350 VUs  1m19.0s/2m00.0s

running (1m20.0s), 350/350 VUs, 225210 complete and 0 interrupted iterations
default   [  67% ] 350/350 VUs  1m20.0s/2m00.0s

running (1m21.0s), 350/350 VUs, 228356 complete and 0 interrupted iterations
default   [  67% ] 350/350 VUs  1m21.0s/2m00.0s

running (1m22.0s), 350/350 VUs, 231411 complete and 0 interrupted iterations
default   [  68% ] 350/350 VUs  1m22.0s/2m00.0s

running (1m23.0s), 350/350 VUs, 234561 complete and 0 interrupted iterations
default   [  69% ] 350/350 VUs  1m23.0s/2m00.0s

running (1m24.0s), 350/350 VUs, 237914 complete and 0 interrupted iterations
default   [  70% ] 350/350 VUs  1m24.0s/2m00.0s

running (1m25.0s), 350/350 VUs, 241162 complete and 0 interrupted iterations
default   [  71% ] 350/350 VUs  1m25.0s/2m00.0s

running (1m26.0s), 350/350 VUs, 244498 complete and 0 interrupted iterations
default   [  72% ] 350/350 VUs  1m26.0s/2m00.0s

running (1m27.0s), 350/350 VUs, 247338 complete and 0 interrupted iterations
default   [  72% ] 350/350 VUs  1m27.0s/2m00.0s

running (1m28.0s), 350/350 VUs, 250327 complete and 0 interrupted iterations
default   [  73% ] 350/350 VUs  1m28.0s/2m00.0s

running (1m29.0s), 350/350 VUs, 253590 complete and 0 interrupted iterations
default   [  74% ] 350/350 VUs  1m29.0s/2m00.0s

running (1m30.0s), 350/350 VUs, 256597 complete and 0 interrupted iterations
default   [  75% ] 350/350 VUs  1m30.0s/2m00.0s

running (1m31.0s), 350/350 VUs, 259954 complete and 0 interrupted iterations
default   [  76% ] 350/350 VUs  1m31.0s/2m00.0s

running (1m32.0s), 350/350 VUs, 263126 complete and 0 interrupted iterations
default   [  77% ] 350/350 VUs  1m32.0s/2m00.0s

running (1m33.0s), 350/350 VUs, 266250 complete and 0 interrupted iterations
default   [  77% ] 350/350 VUs  1m33.0s/2m00.0s

running (1m34.0s), 350/350 VUs, 269430 complete and 0 interrupted iterations
default   [  78% ] 350/350 VUs  1m34.0s/2m00.0s

running (1m35.0s), 350/350 VUs, 272607 complete and 0 interrupted iterations
default   [  79% ] 350/350 VUs  1m35.0s/2m00.0s

running (1m36.0s), 350/350 VUs, 275768 complete and 0 interrupted iterations
default   [  80% ] 350/350 VUs  1m36.0s/2m00.0s

running (1m37.0s), 350/350 VUs, 278685 complete and 0 interrupted iterations
default   [  81% ] 350/350 VUs  1m37.0s/2m00.0s

running (1m38.0s), 350/350 VUs, 281548 complete and 0 interrupted iterations
default   [  82% ] 350/350 VUs  1m38.0s/2m00.0s

running (1m39.0s), 350/350 VUs, 284809 complete and 0 interrupted iterations
default   [  82% ] 350/350 VUs  1m39.0s/2m00.0s

running (1m40.0s), 350/350 VUs, 287903 complete and 0 interrupted iterations
default   [  83% ] 350/350 VUs  1m40.0s/2m00.0s

running (1m41.0s), 350/350 VUs, 291050 complete and 0 interrupted iterations
default   [  84% ] 350/350 VUs  1m41.0s/2m00.0s

running (1m42.0s), 350/350 VUs, 293942 complete and 0 interrupted iterations
default   [  85% ] 350/350 VUs  1m42.0s/2m00.0s

running (1m43.0s), 350/350 VUs, 297161 complete and 0 interrupted iterations
default   [  86% ] 350/350 VUs  1m43.0s/2m00.0s

running (1m44.0s), 350/350 VUs, 300309 complete and 0 interrupted iterations
default   [  87% ] 350/350 VUs  1m44.0s/2m00.0s

running (1m45.0s), 350/350 VUs, 303493 complete and 0 interrupted iterations
default   [  87% ] 350/350 VUs  1m45.0s/2m00.0s

running (1m46.0s), 350/350 VUs, 306693 complete and 0 interrupted iterations
default   [  88% ] 350/350 VUs  1m46.0s/2m00.0s

running (1m47.0s), 350/350 VUs, 309831 complete and 0 interrupted iterations
default   [  89% ] 350/350 VUs  1m47.0s/2m00.0s

running (1m48.0s), 350/350 VUs, 312884 complete and 0 interrupted iterations
default   [  90% ] 350/350 VUs  1m48.0s/2m00.0s

running (1m49.0s), 350/350 VUs, 316210 complete and 0 interrupted iterations
default   [  91% ] 350/350 VUs  1m49.0s/2m00.0s

running (1m50.0s), 350/350 VUs, 319315 complete and 0 interrupted iterations
default   [  92% ] 350/350 VUs  1m50.0s/2m00.0s

running (1m51.0s), 350/350 VUs, 322442 complete and 0 interrupted iterations
default   [  92% ] 350/350 VUs  1m51.0s/2m00.0s

running (1m52.0s), 350/350 VUs, 325449 complete and 0 interrupted iterations
default   [  93% ] 350/350 VUs  1m52.0s/2m00.0s

running (1m53.0s), 350/350 VUs, 328520 complete and 0 interrupted iterations
default   [  94% ] 350/350 VUs  1m53.0s/2m00.0s

running (1m54.0s), 350/350 VUs, 331665 complete and 0 interrupted iterations
default   [  95% ] 350/350 VUs  1m54.0s/2m00.0s

running (1m55.0s), 350/350 VUs, 334871 complete and 0 interrupted iterations
default   [  96% ] 350/350 VUs  1m55.0s/2m00.0s

running (1m56.0s), 350/350 VUs, 337798 complete and 0 interrupted iterations
default   [  97% ] 350/350 VUs  1m56.0s/2m00.0s

running (1m57.0s), 350/350 VUs, 340903 complete and 0 interrupted iterations
default   [  97% ] 350/350 VUs  1m57.0s/2m00.0s

running (1m58.0s), 350/350 VUs, 343908 complete and 0 interrupted iterations
default   [  98% ] 350/350 VUs  1m58.0s/2m00.0s

running (1m59.0s), 350/350 VUs, 346946 complete and 0 interrupted iterations
default   [  99% ] 350/350 VUs  1m59.0s/2m00.0s

running (2m00.0s), 350/350 VUs, 350255 complete and 0 interrupted iterations
default   [ 100% ] 350/350 VUs  2m00.0s/2m00.0s


  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(95)<500' p(95)=213.68ms
    ✓ 'p(99)<1000' p(99)=295.1ms

    http_req_failed
    ✓ 'rate<0.001' rate=0.00%

    http_reqs
    ✓ 'rate>2000' rate=2920.376107/s


  █ TOTAL RESULTS 

    checks_total.......................: 2103858 17522.256642/s
    checks_succeeded...................: 99.99%  2103682 out of 2103858
    checks_failed......................: 0.00%   176 out of 2103858

    ✓ ingest event status is 201
    ✓ ingest event has eventId in response
    ✓ ingest event has status in response
    ✓ ingest event has processedAt in response
    ✗ load: response time < 500ms
      ↳  99% — ✓ 350467 / ✗ 176
    ✓ load: response has body

    HTTP
    http_req_duration.......................................................: avg=104.69ms min=2.32ms med=95.9ms  max=816.72ms p(90)=179.95ms p(95)=213.68ms
      { expected_response:true }............................................: avg=104.69ms min=2.32ms med=95.9ms  max=816.72ms p(90)=179.95ms p(95)=213.68ms
    http_req_failed.........................................................: 0.00%  0 out of 350643
    http_reqs...............................................................: 350643 2920.376107/s

    EXECUTION
    iteration_duration......................................................: avg=104.83ms min=2.44ms med=96.02ms max=817ms    p(90)=180.08ms p(95)=213.82ms
    iterations..............................................................: 350643 2920.376107/s
    vus.....................................................................: 350    min=12          max=350
    vus_max.................................................................: 350    min=350         max=350

    NETWORK
    data_received...........................................................: 64 MB  531 kB/s
    data_sent...............................................................: 141 MB 1.2 MB/s




running (2m00.1s), 000/350 VUs, 350643 complete and 0 interrupted iterations
default ✓ [ 100% ] 000/350 VUs  2m0s


## Stress Test - 2025-11-13 22:22:14


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


running (0m01.0s), 002/500 VUs, 276 complete and 0 interrupted iterations
default   [   1% ] 002/500 VUs  0m01.0s/3m00.0s

running (0m02.0s), 004/500 VUs, 678 complete and 0 interrupted iterations
default   [   1% ] 004/500 VUs  0m02.0s/3m00.0s

running (0m03.0s), 005/500 VUs, 1262 complete and 0 interrupted iterations
default   [   2% ] 005/500 VUs  0m03.0s/3m00.0s

running (0m04.0s), 007/500 VUs, 2863 complete and 0 interrupted iterations
default   [   2% ] 007/500 VUs  0m04.0s/3m00.0s

running (0m05.0s), 009/500 VUs, 4775 complete and 0 interrupted iterations
default   [   3% ] 009/500 VUs  0m05.0s/3m00.0s

running (0m06.0s), 010/500 VUs, 7074 complete and 0 interrupted iterations
default   [   3% ] 010/500 VUs  0m06.0s/3m00.0s

running (0m07.0s), 012/500 VUs, 9343 complete and 0 interrupted iterations
default   [   4% ] 012/500 VUs  0m07.0s/3m00.0s

running (0m08.0s), 013/500 VUs, 11951 complete and 0 interrupted iterations
default   [   4% ] 013/500 VUs  0m08.0s/3m00.0s

running (0m09.0s), 015/500 VUs, 14581 complete and 0 interrupted iterations
default   [   5% ] 015/500 VUs  0m09.0s/3m00.0s

running (0m10.0s), 017/500 VUs, 17301 complete and 0 interrupted iterations
default   [   6% ] 017/500 VUs  0m10.0s/3m00.0s

running (0m11.0s), 018/500 VUs, 17672 complete and 0 interrupted iterations
default   [   6% ] 018/500 VUs  0m11.0s/3m00.0s

running (0m12.0s), 020/500 VUs, 19485 complete and 0 interrupted iterations
default   [   7% ] 020/500 VUs  0m12.0s/3m00.0s

running (0m13.0s), 022/500 VUs, 22109 complete and 0 interrupted iterations
default   [   7% ] 022/500 VUs  0m13.0s/3m00.0s

running (0m14.0s), 023/500 VUs, 24829 complete and 0 interrupted iterations
default   [   8% ] 023/500 VUs  0m14.0s/3m00.0s

running (0m15.0s), 025/500 VUs, 27339 complete and 0 interrupted iterations
default   [   8% ] 025/500 VUs  0m15.0s/3m00.0s

running (0m16.0s), 027/500 VUs, 30370 complete and 0 interrupted iterations
default   [   9% ] 027/500 VUs  0m16.0s/3m00.0s

running (0m17.0s), 028/500 VUs, 32871 complete and 0 interrupted iterations
default   [   9% ] 028/500 VUs  0m17.0s/3m00.0s

running (0m18.0s), 030/500 VUs, 35528 complete and 0 interrupted iterations
default   [  10% ] 030/500 VUs  0m18.0s/3m00.0s

running (0m19.0s), 031/500 VUs, 38022 complete and 0 interrupted iterations
default   [  11% ] 031/500 VUs  0m19.0s/3m00.0s

running (0m20.0s), 033/500 VUs, 41095 complete and 0 interrupted iterations
default   [  11% ] 033/500 VUs  0m20.0s/3m00.0s

running (0m21.0s), 035/500 VUs, 43984 complete and 0 interrupted iterations
default   [  12% ] 035/500 VUs  0m21.0s/3m00.0s

running (0m22.0s), 036/500 VUs, 47122 complete and 0 interrupted iterations
default   [  12% ] 036/500 VUs  0m22.0s/3m00.0s

running (0m23.0s), 038/500 VUs, 50410 complete and 0 interrupted iterations
default   [  13% ] 038/500 VUs  0m23.0s/3m00.0s

running (0m24.0s), 040/500 VUs, 53533 complete and 0 interrupted iterations
default   [  13% ] 040/500 VUs  0m24.0s/3m00.0s

running (0m25.0s), 041/500 VUs, 55926 complete and 0 interrupted iterations
default   [  14% ] 041/500 VUs  0m25.0s/3m00.0s

running (0m26.0s), 043/500 VUs, 58100 complete and 0 interrupted iterations
default   [  14% ] 043/500 VUs  0m26.0s/3m00.0s

running (0m27.0s), 045/500 VUs, 59876 complete and 0 interrupted iterations
default   [  15% ] 045/500 VUs  0m27.0s/3m00.0s

running (0m28.0s), 046/500 VUs, 62290 complete and 0 interrupted iterations
default   [  16% ] 046/500 VUs  0m28.0s/3m00.0s

running (0m29.0s), 048/500 VUs, 65507 complete and 0 interrupted iterations
default   [  16% ] 048/500 VUs  0m29.0s/3m00.0s

running (0m30.0s), 049/500 VUs, 68544 complete and 0 interrupted iterations
default   [  17% ] 049/500 VUs  0m30.0s/3m00.0s

running (0m31.0s), 054/500 VUs, 71720 complete and 0 interrupted iterations
default   [  17% ] 054/500 VUs  0m31.0s/3m00.0s

running (0m32.0s), 059/500 VUs, 74836 complete and 0 interrupted iterations
default   [  18% ] 059/500 VUs  0m32.0s/3m00.0s

running (0m33.0s), 064/500 VUs, 78501 complete and 0 interrupted iterations
default   [  18% ] 064/500 VUs  0m33.0s/3m00.0s

running (0m34.0s), 069/500 VUs, 82014 complete and 0 interrupted iterations
default   [  19% ] 069/500 VUs  0m34.0s/3m00.0s

running (0m35.0s), 074/500 VUs, 85884 complete and 0 interrupted iterations
default   [  19% ] 074/500 VUs  0m35.0s/3m00.0s

running (0m36.0s), 079/500 VUs, 89582 complete and 0 interrupted iterations
default   [  20% ] 079/500 VUs  0m36.0s/3m00.0s

running (0m37.0s), 084/500 VUs, 93191 complete and 0 interrupted iterations
default   [  21% ] 084/500 VUs  0m37.0s/3m00.0s

running (0m38.0s), 089/500 VUs, 97023 complete and 0 interrupted iterations
default   [  21% ] 089/500 VUs  0m38.0s/3m00.0s

running (0m39.0s), 094/500 VUs, 101104 complete and 0 interrupted iterations
default   [  22% ] 094/500 VUs  0m39.0s/3m00.0s

running (0m40.0s), 099/500 VUs, 105002 complete and 0 interrupted iterations
default   [  22% ] 099/500 VUs  0m40.0s/3m00.0s

running (0m41.0s), 104/500 VUs, 109220 complete and 0 interrupted iterations
default   [  23% ] 104/500 VUs  0m41.0s/3m00.0s

running (0m42.0s), 109/500 VUs, 112952 complete and 0 interrupted iterations
default   [  23% ] 109/500 VUs  0m42.0s/3m00.0s

running (0m43.0s), 114/500 VUs, 117279 complete and 0 interrupted iterations
default   [  24% ] 114/500 VUs  0m43.0s/3m00.0s

running (0m44.0s), 119/500 VUs, 121304 complete and 0 interrupted iterations
default   [  24% ] 119/500 VUs  0m44.0s/3m00.0s

running (0m45.0s), 124/500 VUs, 125328 complete and 0 interrupted iterations
default   [  25% ] 124/500 VUs  0m45.0s/3m00.0s

running (0m46.0s), 129/500 VUs, 129432 complete and 0 interrupted iterations
default   [  26% ] 129/500 VUs  0m46.0s/3m00.0s

running (0m47.0s), 134/500 VUs, 133240 complete and 0 interrupted iterations
default   [  26% ] 134/500 VUs  0m47.0s/3m00.0s

running (0m48.0s), 139/500 VUs, 137207 complete and 0 interrupted iterations
default   [  27% ] 139/500 VUs  0m48.0s/3m00.0s

running (0m49.0s), 144/500 VUs, 140637 complete and 0 interrupted iterations
default   [  27% ] 144/500 VUs  0m49.0s/3m00.0s

running (0m50.0s), 149/500 VUs, 144495 complete and 0 interrupted iterations
default   [  28% ] 149/500 VUs  0m50.0s/3m00.0s

running (0m51.0s), 154/500 VUs, 148516 complete and 0 interrupted iterations
default   [  28% ] 154/500 VUs  0m51.0s/3m00.0s

running (0m52.0s), 159/500 VUs, 151322 complete and 0 interrupted iterations
default   [  29% ] 159/500 VUs  0m52.0s/3m00.0s

running (0m53.0s), 164/500 VUs, 155174 complete and 0 interrupted iterations
default   [  29% ] 164/500 VUs  0m53.0s/3m00.0s

running (0m54.0s), 169/500 VUs, 159146 complete and 0 interrupted iterations
default   [  30% ] 169/500 VUs  0m54.0s/3m00.0s

running (0m55.0s), 174/500 VUs, 163184 complete and 0 interrupted iterations
default   [  31% ] 174/500 VUs  0m55.0s/3m00.0s

running (0m56.0s), 179/500 VUs, 167346 complete and 0 interrupted iterations
default   [  31% ] 179/500 VUs  0m56.0s/3m00.0s

running (0m57.0s), 184/500 VUs, 170888 complete and 0 interrupted iterations
default   [  32% ] 184/500 VUs  0m57.0s/3m00.0s

running (0m58.0s), 189/500 VUs, 174967 complete and 0 interrupted iterations
default   [  32% ] 189/500 VUs  0m58.0s/3m00.0s

running (0m59.0s), 194/500 VUs, 179038 complete and 0 interrupted iterations
default   [  33% ] 194/500 VUs  0m59.0s/3m00.0s

running (1m00.0s), 199/500 VUs, 182990 complete and 0 interrupted iterations
default   [  33% ] 199/500 VUs  1m00.0s/3m00.0s

running (1m01.0s), 209/500 VUs, 187181 complete and 0 interrupted iterations
default   [  34% ] 209/500 VUs  1m01.0s/3m00.0s

running (1m02.0s), 219/500 VUs, 190334 complete and 0 interrupted iterations
default   [  34% ] 219/500 VUs  1m02.0s/3m00.0s

running (1m03.0s), 229/500 VUs, 194597 complete and 0 interrupted iterations
default   [  35% ] 229/500 VUs  1m03.0s/3m00.0s

running (1m04.0s), 239/500 VUs, 199077 complete and 0 interrupted iterations
default   [  36% ] 239/500 VUs  1m04.0s/3m00.0s

running (1m05.0s), 249/500 VUs, 203608 complete and 0 interrupted iterations
default   [  36% ] 249/500 VUs  1m05.0s/3m00.0s

running (1m06.0s), 259/500 VUs, 208032 complete and 0 interrupted iterations
default   [  37% ] 259/500 VUs  1m06.0s/3m00.0s

running (1m07.0s), 269/500 VUs, 212156 complete and 0 interrupted iterations
default   [  37% ] 269/500 VUs  1m07.0s/3m00.0s

running (1m08.0s), 279/500 VUs, 216455 complete and 0 interrupted iterations
default   [  38% ] 279/500 VUs  1m08.0s/3m00.0s

running (1m09.0s), 289/500 VUs, 219501 complete and 0 interrupted iterations
default   [  38% ] 289/500 VUs  1m09.0s/3m00.0s

running (1m10.0s), 299/500 VUs, 222705 complete and 0 interrupted iterations
default   [  39% ] 299/500 VUs  1m10.0s/3m00.0s

running (1m11.0s), 309/500 VUs, 226818 complete and 0 interrupted iterations
default   [  39% ] 309/500 VUs  1m11.0s/3m00.0s

running (1m12.0s), 319/500 VUs, 228850 complete and 0 interrupted iterations
default   [  40% ] 319/500 VUs  1m12.0s/3m00.0s

running (1m13.0s), 329/500 VUs, 230090 complete and 0 interrupted iterations
default   [  41% ] 329/500 VUs  1m13.0s/3m00.0s

running (1m14.0s), 339/500 VUs, 233379 complete and 0 interrupted iterations
default   [  41% ] 339/500 VUs  1m14.0s/3m00.0s

running (1m15.0s), 349/500 VUs, 237329 complete and 0 interrupted iterations
default   [  42% ] 349/500 VUs  1m15.0s/3m00.0s

running (1m16.0s), 359/500 VUs, 241180 complete and 0 interrupted iterations
default   [  42% ] 359/500 VUs  1m16.0s/3m00.0s

running (1m17.0s), 369/500 VUs, 244799 complete and 0 interrupted iterations
default   [  43% ] 369/500 VUs  1m17.0s/3m00.0s

running (1m18.0s), 379/500 VUs, 249001 complete and 0 interrupted iterations
default   [  43% ] 379/500 VUs  1m18.0s/3m00.0s

running (1m19.0s), 389/500 VUs, 252705 complete and 0 interrupted iterations
default   [  44% ] 389/500 VUs  1m19.0s/3m00.0s

running (1m20.0s), 399/500 VUs, 256547 complete and 0 interrupted iterations
default   [  44% ] 399/500 VUs  1m20.0s/3m00.0s

running (1m21.0s), 409/500 VUs, 260698 complete and 0 interrupted iterations
default   [  45% ] 409/500 VUs  1m21.0s/3m00.0s

running (1m22.0s), 419/500 VUs, 264220 complete and 0 interrupted iterations
default   [  46% ] 419/500 VUs  1m22.0s/3m00.0s

running (1m23.0s), 429/500 VUs, 267787 complete and 0 interrupted iterations
default   [  46% ] 429/500 VUs  1m23.0s/3m00.0s

running (1m24.0s), 439/500 VUs, 271086 complete and 0 interrupted iterations
default   [  47% ] 439/500 VUs  1m24.0s/3m00.0s

running (1m25.0s), 449/500 VUs, 275261 complete and 0 interrupted iterations
default   [  47% ] 449/500 VUs  1m25.0s/3m00.0s

running (1m26.0s), 459/500 VUs, 278982 complete and 0 interrupted iterations
default   [  48% ] 459/500 VUs  1m26.0s/3m00.0s

running (1m27.0s), 469/500 VUs, 282589 complete and 0 interrupted iterations
default   [  48% ] 469/500 VUs  1m27.0s/3m00.0s

running (1m28.0s), 479/500 VUs, 286923 complete and 0 interrupted iterations
default   [  49% ] 479/500 VUs  1m28.0s/3m00.0s

running (1m29.0s), 489/500 VUs, 290035 complete and 0 interrupted iterations
default   [  49% ] 489/500 VUs  1m29.0s/3m00.0s

running (1m30.0s), 499/500 VUs, 292123 complete and 0 interrupted iterations
default   [  50% ] 499/500 VUs  1m30.0s/3m00.0s

running (1m31.0s), 500/500 VUs, 293469 complete and 0 interrupted iterations
default   [  51% ] 500/500 VUs  1m31.0s/3m00.0s

running (1m32.0s), 500/500 VUs, 296960 complete and 0 interrupted iterations
default   [  51% ] 500/500 VUs  1m32.0s/3m00.0s

running (1m33.0s), 500/500 VUs, 300961 complete and 0 interrupted iterations
default   [  52% ] 500/500 VUs  1m33.0s/3m00.0s

running (1m34.0s), 500/500 VUs, 305321 complete and 0 interrupted iterations
default   [  52% ] 500/500 VUs  1m34.0s/3m00.0s

running (1m35.0s), 500/500 VUs, 309753 complete and 0 interrupted iterations
default   [  53% ] 500/500 VUs  1m35.0s/3m00.0s

running (1m36.0s), 500/500 VUs, 314756 complete and 0 interrupted iterations
default   [  53% ] 500/500 VUs  1m36.0s/3m00.0s

running (1m37.0s), 500/500 VUs, 319040 complete and 0 interrupted iterations
default   [  54% ] 500/500 VUs  1m37.0s/3m00.0s

running (1m38.0s), 500/500 VUs, 322826 complete and 0 interrupted iterations
default   [  54% ] 500/500 VUs  1m38.0s/3m00.0s

running (1m39.0s), 500/500 VUs, 326815 complete and 0 interrupted iterations
default   [  55% ] 500/500 VUs  1m39.0s/3m00.0s

running (1m40.0s), 500/500 VUs, 330969 complete and 0 interrupted iterations
default   [  56% ] 500/500 VUs  1m40.0s/3m00.0s

running (1m41.0s), 500/500 VUs, 335166 complete and 0 interrupted iterations
default   [  56% ] 500/500 VUs  1m41.0s/3m00.0s

running (1m42.0s), 500/500 VUs, 338860 complete and 0 interrupted iterations
default   [  57% ] 500/500 VUs  1m42.0s/3m00.0s

running (1m43.0s), 500/500 VUs, 343327 complete and 0 interrupted iterations
default   [  57% ] 500/500 VUs  1m43.0s/3m00.0s

running (1m44.0s), 500/500 VUs, 347686 complete and 0 interrupted iterations
default   [  58% ] 500/500 VUs  1m44.0s/3m00.0s

running (1m45.0s), 500/500 VUs, 351948 complete and 0 interrupted iterations
default   [  58% ] 500/500 VUs  1m45.0s/3m00.0s

running (1m46.0s), 500/500 VUs, 356341 complete and 0 interrupted iterations
default   [  59% ] 500/500 VUs  1m46.0s/3m00.0s

running (1m47.0s), 500/500 VUs, 360604 complete and 0 interrupted iterations
default   [  59% ] 500/500 VUs  1m47.0s/3m00.0s

running (1m48.0s), 500/500 VUs, 364850 complete and 0 interrupted iterations
default   [  60% ] 500/500 VUs  1m48.0s/3m00.0s

running (1m49.0s), 500/500 VUs, 368761 complete and 0 interrupted iterations
default   [  61% ] 500/500 VUs  1m49.0s/3m00.0s

running (1m50.0s), 500/500 VUs, 371387 complete and 0 interrupted iterations
default   [  61% ] 500/500 VUs  1m50.0s/3m00.0s

running (1m51.0s), 500/500 VUs, 374609 complete and 0 interrupted iterations
default   [  62% ] 500/500 VUs  1m51.0s/3m00.0s

running (1m52.0s), 500/500 VUs, 378478 complete and 0 interrupted iterations
default   [  62% ] 500/500 VUs  1m52.0s/3m00.0s

running (1m53.0s), 500/500 VUs, 382509 complete and 0 interrupted iterations
default   [  63% ] 500/500 VUs  1m53.0s/3m00.0s

running (1m54.0s), 500/500 VUs, 386707 complete and 0 interrupted iterations
default   [  63% ] 500/500 VUs  1m54.0s/3m00.0s

running (1m55.0s), 500/500 VUs, 390963 complete and 0 interrupted iterations
default   [  64% ] 500/500 VUs  1m55.0s/3m00.0s

running (1m56.0s), 500/500 VUs, 395187 complete and 0 interrupted iterations
default   [  64% ] 500/500 VUs  1m56.0s/3m00.0s

running (1m57.0s), 500/500 VUs, 398849 complete and 0 interrupted iterations
default   [  65% ] 500/500 VUs  1m57.0s/3m00.0s

running (1m58.0s), 500/500 VUs, 402958 complete and 0 interrupted iterations
default   [  66% ] 500/500 VUs  1m58.0s/3m00.0s

running (1m59.0s), 500/500 VUs, 406979 complete and 0 interrupted iterations
default   [  66% ] 500/500 VUs  1m59.0s/3m00.0s

running (2m00.0s), 500/500 VUs, 411253 complete and 0 interrupted iterations
default   [  67% ] 500/500 VUs  2m00.0s/3m00.0s

running (2m01.0s), 500/500 VUs, 415235 complete and 0 interrupted iterations
default   [  67% ] 500/500 VUs  2m01.0s/3m00.0s

running (2m02.0s), 500/500 VUs, 419097 complete and 0 interrupted iterations
default   [  68% ] 500/500 VUs  2m02.0s/3m00.0s

running (2m03.0s), 500/500 VUs, 423495 complete and 0 interrupted iterations
default   [  68% ] 500/500 VUs  2m03.0s/3m00.0s

running (2m04.0s), 500/500 VUs, 428008 complete and 0 interrupted iterations
default   [  69% ] 500/500 VUs  2m04.0s/3m00.0s

running (2m05.0s), 500/500 VUs, 431997 complete and 0 interrupted iterations
default   [  69% ] 500/500 VUs  2m05.0s/3m00.0s

running (2m06.0s), 500/500 VUs, 435989 complete and 0 interrupted iterations
default   [  70% ] 500/500 VUs  2m06.0s/3m00.0s

running (2m07.0s), 500/500 VUs, 440296 complete and 0 interrupted iterations
default   [  71% ] 500/500 VUs  2m07.0s/3m00.0s

running (2m08.0s), 500/500 VUs, 444593 complete and 0 interrupted iterations
default   [  71% ] 500/500 VUs  2m08.0s/3m00.0s

running (2m09.0s), 500/500 VUs, 449026 complete and 0 interrupted iterations
default   [  72% ] 500/500 VUs  2m09.0s/3m00.0s

running (2m10.0s), 500/500 VUs, 453445 complete and 0 interrupted iterations
default   [  72% ] 500/500 VUs  2m10.0s/3m00.0s

running (2m11.0s), 500/500 VUs, 458088 complete and 0 interrupted iterations
default   [  73% ] 500/500 VUs  2m11.0s/3m00.0s

running (2m12.0s), 500/500 VUs, 462339 complete and 0 interrupted iterations
default   [  73% ] 500/500 VUs  2m12.0s/3m00.0s

running (2m13.0s), 500/500 VUs, 466760 complete and 0 interrupted iterations
default   [  74% ] 500/500 VUs  2m13.0s/3m00.0s

running (2m14.0s), 500/500 VUs, 471461 complete and 0 interrupted iterations
default   [  74% ] 500/500 VUs  2m14.0s/3m00.0s

running (2m15.0s), 500/500 VUs, 476007 complete and 0 interrupted iterations
default   [  75% ] 500/500 VUs  2m15.0s/3m00.0s

running (2m16.0s), 500/500 VUs, 480712 complete and 0 interrupted iterations
default   [  76% ] 500/500 VUs  2m16.0s/3m00.0s

running (2m17.0s), 500/500 VUs, 485246 complete and 0 interrupted iterations
default   [  76% ] 500/500 VUs  2m17.0s/3m00.0s

running (2m18.0s), 500/500 VUs, 489855 complete and 0 interrupted iterations
default   [  77% ] 500/500 VUs  2m18.0s/3m00.0s

running (2m19.0s), 500/500 VUs, 494681 complete and 0 interrupted iterations
default   [  77% ] 500/500 VUs  2m19.0s/3m00.0s

running (2m20.0s), 500/500 VUs, 499098 complete and 0 interrupted iterations
default   [  78% ] 500/500 VUs  2m20.0s/3m00.0s

running (2m21.0s), 500/500 VUs, 503604 complete and 0 interrupted iterations
default   [  78% ] 500/500 VUs  2m21.0s/3m00.0s

running (2m22.0s), 500/500 VUs, 507541 complete and 0 interrupted iterations
default   [  79% ] 500/500 VUs  2m22.0s/3m00.0s

running (2m23.0s), 500/500 VUs, 512333 complete and 0 interrupted iterations
default   [  79% ] 500/500 VUs  2m23.0s/3m00.0s

running (2m24.0s), 500/500 VUs, 516693 complete and 0 interrupted iterations
default   [  80% ] 500/500 VUs  2m24.0s/3m00.0s

running (2m25.0s), 500/500 VUs, 521415 complete and 0 interrupted iterations
default   [  81% ] 500/500 VUs  2m25.0s/3m00.0s

running (2m26.0s), 500/500 VUs, 525914 complete and 0 interrupted iterations
default   [  81% ] 500/500 VUs  2m26.0s/3m00.0s

running (2m27.0s), 500/500 VUs, 529884 complete and 0 interrupted iterations
default   [  82% ] 500/500 VUs  2m27.0s/3m00.0s

running (2m28.0s), 500/500 VUs, 533026 complete and 0 interrupted iterations
default   [  82% ] 500/500 VUs  2m28.0s/3m00.0s

running (2m29.0s), 500/500 VUs, 537141 complete and 0 interrupted iterations
default   [  83% ] 500/500 VUs  2m29.0s/3m00.0s

running (2m30.0s), 500/500 VUs, 541043 complete and 0 interrupted iterations
default   [  83% ] 500/500 VUs  2m30.0s/3m00.0s

running (2m31.0s), 500/500 VUs, 545651 complete and 0 interrupted iterations
default   [  84% ] 500/500 VUs  2m31.0s/3m00.0s

running (2m32.0s), 500/500 VUs, 549061 complete and 0 interrupted iterations
default   [  84% ] 500/500 VUs  2m32.0s/3m00.0s

running (2m33.0s), 500/500 VUs, 553067 complete and 0 interrupted iterations
default   [  85% ] 500/500 VUs  2m33.0s/3m00.0s

running (2m34.0s), 500/500 VUs, 556961 complete and 0 interrupted iterations
default   [  86% ] 500/500 VUs  2m34.0s/3m00.0s

running (2m35.0s), 500/500 VUs, 561437 complete and 0 interrupted iterations
default   [  86% ] 500/500 VUs  2m35.0s/3m00.0s

running (2m36.0s), 500/500 VUs, 566048 complete and 0 interrupted iterations
default   [  87% ] 500/500 VUs  2m36.0s/3m00.0s

running (2m37.0s), 500/500 VUs, 570504 complete and 0 interrupted iterations
default   [  87% ] 500/500 VUs  2m37.0s/3m00.0s

running (2m38.0s), 500/500 VUs, 574878 complete and 0 interrupted iterations
default   [  88% ] 500/500 VUs  2m38.0s/3m00.0s

running (2m39.0s), 500/500 VUs, 578575 complete and 0 interrupted iterations
default   [  88% ] 500/500 VUs  2m39.0s/3m00.0s

running (2m40.0s), 500/500 VUs, 583002 complete and 0 interrupted iterations
default   [  89% ] 500/500 VUs  2m40.0s/3m00.0s

running (2m41.0s), 500/500 VUs, 587113 complete and 0 interrupted iterations
default   [  89% ] 500/500 VUs  2m41.0s/3m00.0s

running (2m42.0s), 500/500 VUs, 591252 complete and 0 interrupted iterations
default   [  90% ] 500/500 VUs  2m42.0s/3m00.0s

running (2m43.0s), 500/500 VUs, 596172 complete and 0 interrupted iterations
default   [  91% ] 500/500 VUs  2m43.0s/3m00.0s

running (2m44.0s), 500/500 VUs, 600516 complete and 0 interrupted iterations
default   [  91% ] 500/500 VUs  2m44.0s/3m00.0s

running (2m45.0s), 500/500 VUs, 605356 complete and 0 interrupted iterations
default   [  92% ] 500/500 VUs  2m45.0s/3m00.0s

running (2m46.0s), 500/500 VUs, 609906 complete and 0 interrupted iterations
default   [  92% ] 500/500 VUs  2m46.0s/3m00.0s

running (2m47.0s), 500/500 VUs, 614029 complete and 0 interrupted iterations
default   [  93% ] 500/500 VUs  2m47.0s/3m00.0s

running (2m48.0s), 500/500 VUs, 618187 complete and 0 interrupted iterations
default   [  93% ] 500/500 VUs  2m48.0s/3m00.0s

running (2m49.0s), 500/500 VUs, 622855 complete and 0 interrupted iterations
default   [  94% ] 500/500 VUs  2m49.0s/3m00.0s

running (2m50.0s), 500/500 VUs, 627292 complete and 0 interrupted iterations
default   [  94% ] 500/500 VUs  2m50.0s/3m00.0s

running (2m51.0s), 500/500 VUs, 631850 complete and 0 interrupted iterations
default   [  95% ] 500/500 VUs  2m51.0s/3m00.0s

running (2m52.0s), 500/500 VUs, 636142 complete and 0 interrupted iterations
default   [  96% ] 500/500 VUs  2m52.0s/3m00.0s

running (2m53.0s), 500/500 VUs, 640887 complete and 0 interrupted iterations
default   [  96% ] 500/500 VUs  2m53.0s/3m00.0s

running (2m54.0s), 500/500 VUs, 645296 complete and 0 interrupted iterations
default   [  97% ] 500/500 VUs  2m54.0s/3m00.0s

running (2m55.0s), 500/500 VUs, 649799 complete and 0 interrupted iterations
default   [  97% ] 500/500 VUs  2m55.0s/3m00.0s

running (2m56.0s), 500/500 VUs, 654117 complete and 0 interrupted iterations
default   [  98% ] 500/500 VUs  2m56.0s/3m00.0s

running (2m57.0s), 500/500 VUs, 658609 complete and 0 interrupted iterations
default   [  98% ] 500/500 VUs  2m57.0s/3m00.0s

running (2m58.0s), 500/500 VUs, 663030 complete and 0 interrupted iterations
default   [  99% ] 500/500 VUs  2m58.0s/3m00.0s

running (2m59.0s), 500/500 VUs, 667471 complete and 0 interrupted iterations
default   [  99% ] 500/500 VUs  2m59.0s/3m00.0s

running (3m00.0s), 500/500 VUs, 671782 complete and 0 interrupted iterations
default   [ 100% ] 500/500 VUs  3m00.0s/3m00.0s


  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(95)<2000' p(95)=161.09ms
    ✓ 'p(99)<5000' p(99)=227.82ms

    http_req_failed
    ✓ 'rate<0.05' rate=0.00%


  █ TOTAL RESULTS 

    checks_total.......................: 4034688 22405.118149/s
    checks_succeeded...................: 100.00% 4034688 out of 4034688
    checks_failed......................: 0.00%   0 out of 4034688

    ✓ ingest event status is 201
    ✓ ingest event has eventId in response
    ✓ ingest event has status in response
    ✓ ingest event has processedAt in response
    ✓ stress: response time < 2000ms
    ✓ stress: response has body

    HTTP
    http_req_duration.......................................................: avg=88.98ms min=1.86ms med=99.66ms max=864.7ms  p(90)=141.49ms p(95)=161.09ms
      { expected_response:true }............................................: avg=88.98ms min=1.86ms med=99.66ms max=864.7ms  p(90)=141.49ms p(95)=161.09ms
    http_req_failed.........................................................: 0.00%  0 out of 672448
    http_reqs...............................................................: 672448 3734.186358/s

    EXECUTION
    iteration_duration......................................................: avg=89.2ms  min=1.97ms med=99.85ms max=864.84ms p(90)=141.74ms p(95)=161.44ms
    iterations..............................................................: 672448 3734.186358/s
    vus.....................................................................: 500    min=2           max=500
    vus_max.................................................................: 500    min=500         max=500

    NETWORK
    data_received...........................................................: 122 MB 679 kB/s
    data_sent...............................................................: 271 MB 1.5 MB/s




running (3m00.1s), 000/500 VUs, 672448 complete and 0 interrupted iterations
default ✓ [ 100% ] 000/500 VUs  3m0s


## Spike Test - 2025-11-13 22:25:09


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


running (0m01.0s), 003/500 VUs, 479 complete and 0 interrupted iterations
default   [   1% ] 003/500 VUs  0m01.0s/2m30.0s

running (0m02.0s), 005/500 VUs, 1755 complete and 0 interrupted iterations
default   [   1% ] 005/500 VUs  0m02.0s/2m30.0s

running (0m03.0s), 008/500 VUs, 3637 complete and 0 interrupted iterations
default   [   2% ] 008/500 VUs  0m03.0s/2m30.0s

running (0m04.0s), 010/500 VUs, 5734 complete and 0 interrupted iterations
default   [   3% ] 010/500 VUs  0m04.0s/2m30.0s

running (0m05.0s), 013/500 VUs, 7989 complete and 0 interrupted iterations
default   [   3% ] 013/500 VUs  0m05.0s/2m30.0s

running (0m06.0s), 015/500 VUs, 10158 complete and 0 interrupted iterations
default   [   4% ] 015/500 VUs  0m06.0s/2m30.0s

running (0m07.0s), 018/500 VUs, 12473 complete and 0 interrupted iterations
default   [   5% ] 018/500 VUs  0m07.0s/2m30.0s

running (0m08.0s), 020/500 VUs, 14882 complete and 0 interrupted iterations
default   [   5% ] 020/500 VUs  0m08.0s/2m30.0s

running (0m09.0s), 022/500 VUs, 17350 complete and 0 interrupted iterations
default   [   6% ] 022/500 VUs  0m09.0s/2m30.0s

running (0m10.0s), 025/500 VUs, 19801 complete and 0 interrupted iterations
default   [   7% ] 025/500 VUs  0m10.0s/2m30.0s

running (0m11.0s), 027/500 VUs, 21531 complete and 0 interrupted iterations
default   [   7% ] 027/500 VUs  0m11.0s/2m30.0s

running (0m12.0s), 030/500 VUs, 23995 complete and 0 interrupted iterations
default   [   8% ] 030/500 VUs  0m12.0s/2m30.0s

running (0m13.0s), 032/500 VUs, 26504 complete and 0 interrupted iterations
default   [   9% ] 032/500 VUs  0m13.0s/2m30.0s

running (0m14.0s), 035/500 VUs, 28790 complete and 0 interrupted iterations
default   [   9% ] 035/500 VUs  0m14.0s/2m30.0s

running (0m15.0s), 037/500 VUs, 31004 complete and 0 interrupted iterations
default   [  10% ] 037/500 VUs  0m15.0s/2m30.0s

running (0m16.0s), 040/500 VUs, 31683 complete and 0 interrupted iterations
default   [  11% ] 040/500 VUs  0m16.0s/2m30.0s

running (0m17.0s), 042/500 VUs, 33279 complete and 0 interrupted iterations
default   [  11% ] 042/500 VUs  0m17.0s/2m30.0s

running (0m18.0s), 045/500 VUs, 35834 complete and 0 interrupted iterations
default   [  12% ] 045/500 VUs  0m18.0s/2m30.0s

running (0m19.0s), 047/500 VUs, 38275 complete and 0 interrupted iterations
default   [  13% ] 047/500 VUs  0m19.0s/2m30.0s

running (0m20.0s), 049/500 VUs, 40852 complete and 0 interrupted iterations
default   [  13% ] 049/500 VUs  0m20.0s/2m30.0s

running (0m21.0s), 071/500 VUs, 43166 complete and 0 interrupted iterations
default   [  14% ] 071/500 VUs  0m21.0s/2m30.0s

running (0m22.0s), 094/500 VUs, 45807 complete and 0 interrupted iterations
default   [  15% ] 094/500 VUs  0m22.0s/2m30.0s

running (0m23.0s), 116/500 VUs, 48738 complete and 0 interrupted iterations
default   [  15% ] 116/500 VUs  0m23.0s/2m30.0s

running (0m24.0s), 139/500 VUs, 51104 complete and 0 interrupted iterations
default   [  16% ] 139/500 VUs  0m24.0s/2m30.0s

running (0m25.0s), 161/500 VUs, 54110 complete and 0 interrupted iterations
default   [  17% ] 161/500 VUs  0m25.0s/2m30.0s

running (0m26.0s), 184/500 VUs, 56655 complete and 0 interrupted iterations
default   [  17% ] 184/500 VUs  0m26.0s/2m30.0s

running (0m27.0s), 206/500 VUs, 59595 complete and 0 interrupted iterations
default   [  18% ] 206/500 VUs  0m27.0s/2m30.0s

running (0m28.0s), 229/500 VUs, 62570 complete and 0 interrupted iterations
default   [  19% ] 229/500 VUs  0m28.0s/2m30.0s

running (0m29.0s), 251/500 VUs, 64998 complete and 0 interrupted iterations
default   [  19% ] 251/500 VUs  0m29.0s/2m30.0s

running (0m30.0s), 274/500 VUs, 68499 complete and 0 interrupted iterations
default   [  20% ] 274/500 VUs  0m30.0s/2m30.0s

running (0m31.0s), 296/500 VUs, 71504 complete and 0 interrupted iterations
default   [  21% ] 296/500 VUs  0m31.0s/2m30.0s

running (0m32.0s), 319/500 VUs, 75035 complete and 0 interrupted iterations
default   [  21% ] 319/500 VUs  0m32.0s/2m30.0s

running (0m33.0s), 341/500 VUs, 77972 complete and 0 interrupted iterations
default   [  22% ] 341/500 VUs  0m33.0s/2m30.0s

running (0m34.0s), 364/500 VUs, 81126 complete and 0 interrupted iterations
default   [  23% ] 364/500 VUs  0m34.0s/2m30.0s

running (0m35.0s), 386/500 VUs, 84478 complete and 0 interrupted iterations
default   [  23% ] 386/500 VUs  0m35.0s/2m30.0s

running (0m36.0s), 409/500 VUs, 86869 complete and 0 interrupted iterations
default   [  24% ] 409/500 VUs  0m36.0s/2m30.0s

running (0m37.0s), 431/500 VUs, 89843 complete and 0 interrupted iterations
default   [  25% ] 431/500 VUs  0m37.0s/2m30.0s

running (0m38.0s), 454/500 VUs, 92754 complete and 0 interrupted iterations
default   [  25% ] 454/500 VUs  0m38.0s/2m30.0s

running (0m39.0s), 476/500 VUs, 95909 complete and 0 interrupted iterations
default   [  26% ] 476/500 VUs  0m39.0s/2m30.0s

running (0m40.0s), 499/500 VUs, 99175 complete and 0 interrupted iterations
default   [  27% ] 499/500 VUs  0m40.0s/2m30.0s

running (0m41.0s), 480/500 VUs, 102251 complete and 0 interrupted iterations
default   [  27% ] 480/500 VUs  0m41.0s/2m30.0s

running (0m42.0s), 459/500 VUs, 105616 complete and 0 interrupted iterations
default   [  28% ] 459/500 VUs  0m42.0s/2m30.0s

running (0m43.0s), 437/500 VUs, 108995 complete and 0 interrupted iterations
default   [  29% ] 437/500 VUs  0m43.0s/2m30.0s

running (0m44.0s), 414/500 VUs, 112349 complete and 0 interrupted iterations
default   [  29% ] 414/500 VUs  0m44.0s/2m30.0s

running (0m45.0s), 391/500 VUs, 115784 complete and 0 interrupted iterations
default   [  30% ] 391/500 VUs  0m45.0s/2m30.0s

running (0m46.0s), 370/500 VUs, 118607 complete and 0 interrupted iterations
default   [  31% ] 370/500 VUs  0m46.0s/2m30.0s

running (0m47.0s), 346/500 VUs, 121924 complete and 0 interrupted iterations
default   [  31% ] 346/500 VUs  0m47.0s/2m30.0s

running (0m48.0s), 323/500 VUs, 125250 complete and 0 interrupted iterations
default   [  32% ] 323/500 VUs  0m48.0s/2m30.0s

running (0m49.0s), 301/500 VUs, 128445 complete and 0 interrupted iterations
default   [  33% ] 301/500 VUs  0m49.0s/2m30.0s

running (0m50.0s), 278/500 VUs, 131591 complete and 0 interrupted iterations
default   [  33% ] 278/500 VUs  0m50.0s/2m30.0s

running (0m51.0s), 255/500 VUs, 134632 complete and 0 interrupted iterations
default   [  34% ] 255/500 VUs  0m51.0s/2m30.0s

running (0m52.0s), 232/500 VUs, 137962 complete and 0 interrupted iterations
default   [  35% ] 232/500 VUs  0m52.0s/2m30.0s

running (0m53.0s), 209/500 VUs, 141177 complete and 0 interrupted iterations
default   [  35% ] 209/500 VUs  0m53.0s/2m30.0s

running (0m54.0s), 188/500 VUs, 144015 complete and 0 interrupted iterations
default   [  36% ] 188/500 VUs  0m54.0s/2m30.0s

running (0m55.0s), 164/500 VUs, 146824 complete and 0 interrupted iterations
default   [  37% ] 164/500 VUs  0m55.0s/2m30.0s

running (0m56.0s), 143/500 VUs, 149261 complete and 0 interrupted iterations
default   [  37% ] 143/500 VUs  0m56.0s/2m30.0s

running (0m57.0s), 120/500 VUs, 151930 complete and 0 interrupted iterations
default   [  38% ] 120/500 VUs  0m57.0s/2m30.0s

running (0m58.0s), 098/500 VUs, 154339 complete and 0 interrupted iterations
default   [  39% ] 098/500 VUs  0m58.0s/2m30.0s

running (0m59.0s), 075/500 VUs, 156788 complete and 0 interrupted iterations
default   [  39% ] 075/500 VUs  0m59.0s/2m30.0s

running (1m00.0s), 051/500 VUs, 159398 complete and 0 interrupted iterations
default   [  40% ] 051/500 VUs  1m00.0s/2m30.0s

running (1m01.0s), 071/500 VUs, 162098 complete and 0 interrupted iterations
default   [  41% ] 071/500 VUs  1m01.0s/2m30.0s

running (1m02.0s), 094/500 VUs, 164925 complete and 0 interrupted iterations
default   [  41% ] 094/500 VUs  1m02.0s/2m30.0s

running (1m03.0s), 116/500 VUs, 167772 complete and 0 interrupted iterations
default   [  42% ] 116/500 VUs  1m03.0s/2m30.0s

running (1m04.0s), 139/500 VUs, 170303 complete and 0 interrupted iterations
default   [  43% ] 139/500 VUs  1m04.0s/2m30.0s

running (1m05.0s), 161/500 VUs, 173210 complete and 0 interrupted iterations
default   [  43% ] 161/500 VUs  1m05.0s/2m30.0s

running (1m06.0s), 184/500 VUs, 176038 complete and 0 interrupted iterations
default   [  44% ] 184/500 VUs  1m06.0s/2m30.0s

running (1m07.0s), 206/500 VUs, 179083 complete and 0 interrupted iterations
default   [  45% ] 206/500 VUs  1m07.0s/2m30.0s

running (1m08.0s), 229/500 VUs, 182238 complete and 0 interrupted iterations
default   [  45% ] 229/500 VUs  1m08.0s/2m30.0s

running (1m09.0s), 251/500 VUs, 185346 complete and 0 interrupted iterations
default   [  46% ] 251/500 VUs  1m09.0s/2m30.0s

running (1m10.0s), 274/500 VUs, 188589 complete and 0 interrupted iterations
default   [  47% ] 274/500 VUs  1m10.0s/2m30.0s

running (1m11.0s), 296/500 VUs, 191599 complete and 0 interrupted iterations
default   [  47% ] 296/500 VUs  1m11.0s/2m30.0s

running (1m12.0s), 319/500 VUs, 194701 complete and 0 interrupted iterations
default   [  48% ] 319/500 VUs  1m12.0s/2m30.0s

running (1m13.0s), 341/500 VUs, 197783 complete and 0 interrupted iterations
default   [  49% ] 341/500 VUs  1m13.0s/2m30.0s

running (1m14.0s), 364/500 VUs, 200956 complete and 0 interrupted iterations
default   [  49% ] 364/500 VUs  1m14.0s/2m30.0s

running (1m15.0s), 386/500 VUs, 203782 complete and 0 interrupted iterations
default   [  50% ] 386/500 VUs  1m15.0s/2m30.0s

running (1m16.0s), 409/500 VUs, 205548 complete and 0 interrupted iterations
default   [  51% ] 409/500 VUs  1m16.0s/2m30.0s

running (1m17.0s), 431/500 VUs, 208141 complete and 0 interrupted iterations
default   [  51% ] 431/500 VUs  1m17.0s/2m30.0s

running (1m18.0s), 454/500 VUs, 210146 complete and 0 interrupted iterations
default   [  52% ] 454/500 VUs  1m18.0s/2m30.0s

running (1m19.0s), 476/500 VUs, 212689 complete and 0 interrupted iterations
default   [  53% ] 476/500 VUs  1m19.0s/2m30.0s

running (1m20.0s), 499/500 VUs, 215881 complete and 0 interrupted iterations
default   [  53% ] 499/500 VUs  1m20.0s/2m30.0s

running (1m21.0s), 481/500 VUs, 218896 complete and 0 interrupted iterations
default   [  54% ] 481/500 VUs  1m21.0s/2m30.0s

running (1m22.0s), 459/500 VUs, 221876 complete and 0 interrupted iterations
default   [  55% ] 459/500 VUs  1m22.0s/2m30.0s

running (1m23.0s), 436/500 VUs, 225047 complete and 0 interrupted iterations
default   [  55% ] 436/500 VUs  1m23.0s/2m30.0s

running (1m24.0s), 413/500 VUs, 227982 complete and 0 interrupted iterations
default   [  56% ] 413/500 VUs  1m24.0s/2m30.0s

running (1m25.0s), 391/500 VUs, 231068 complete and 0 interrupted iterations
default   [  57% ] 391/500 VUs  1m25.0s/2m30.0s

running (1m26.0s), 368/500 VUs, 234201 complete and 0 interrupted iterations
default   [  57% ] 368/500 VUs  1m26.0s/2m30.0s

running (1m27.0s), 344/500 VUs, 237266 complete and 0 interrupted iterations
default   [  58% ] 344/500 VUs  1m27.0s/2m30.0s

running (1m28.0s), 323/500 VUs, 240562 complete and 0 interrupted iterations
default   [  59% ] 323/500 VUs  1m28.0s/2m30.0s

running (1m29.0s), 300/500 VUs, 243814 complete and 0 interrupted iterations
default   [  59% ] 300/500 VUs  1m29.0s/2m30.0s

running (1m30.0s), 279/500 VUs, 246990 complete and 0 interrupted iterations
default   [  60% ] 279/500 VUs  1m30.0s/2m30.0s

running (1m31.0s), 255/500 VUs, 250195 complete and 0 interrupted iterations
default   [  61% ] 255/500 VUs  1m31.0s/2m30.0s

running (1m32.0s), 234/500 VUs, 253268 complete and 0 interrupted iterations
default   [  61% ] 234/500 VUs  1m32.0s/2m30.0s

running (1m33.0s), 210/500 VUs, 256399 complete and 0 interrupted iterations
default   [  62% ] 210/500 VUs  1m33.0s/2m30.0s

running (1m34.0s), 188/500 VUs, 259476 complete and 0 interrupted iterations
default   [  63% ] 188/500 VUs  1m34.0s/2m30.0s

running (1m35.0s), 167/500 VUs, 262641 complete and 0 interrupted iterations
default   [  63% ] 167/500 VUs  1m35.0s/2m30.0s

running (1m36.0s), 142/500 VUs, 265057 complete and 0 interrupted iterations
default   [  64% ] 142/500 VUs  1m36.0s/2m30.0s

running (1m37.0s), 120/500 VUs, 267949 complete and 0 interrupted iterations
default   [  65% ] 120/500 VUs  1m37.0s/2m30.0s

running (1m38.0s), 098/500 VUs, 270637 complete and 0 interrupted iterations
default   [  65% ] 098/500 VUs  1m38.0s/2m30.0s

running (1m39.0s), 074/500 VUs, 273299 complete and 0 interrupted iterations
default   [  66% ] 074/500 VUs  1m39.0s/2m30.0s

running (1m40.0s), 052/500 VUs, 275975 complete and 0 interrupted iterations
default   [  67% ] 052/500 VUs  1m40.0s/2m30.0s

running (1m41.0s), 071/500 VUs, 278658 complete and 0 interrupted iterations
default   [  67% ] 071/500 VUs  1m41.0s/2m30.0s

running (1m42.0s), 094/500 VUs, 281441 complete and 0 interrupted iterations
default   [  68% ] 094/500 VUs  1m42.0s/2m30.0s

running (1m43.0s), 116/500 VUs, 284250 complete and 0 interrupted iterations
default   [  69% ] 116/500 VUs  1m43.0s/2m30.0s

running (1m44.0s), 139/500 VUs, 287150 complete and 0 interrupted iterations
default   [  69% ] 139/500 VUs  1m44.0s/2m30.0s

running (1m45.0s), 161/500 VUs, 290277 complete and 0 interrupted iterations
default   [  70% ] 161/500 VUs  1m45.0s/2m30.0s

running (1m46.0s), 184/500 VUs, 293245 complete and 0 interrupted iterations
default   [  71% ] 184/500 VUs  1m46.0s/2m30.0s

running (1m47.0s), 206/500 VUs, 296387 complete and 0 interrupted iterations
default   [  71% ] 206/500 VUs  1m47.0s/2m30.0s

running (1m48.0s), 229/500 VUs, 299610 complete and 0 interrupted iterations
default   [  72% ] 229/500 VUs  1m48.0s/2m30.0s

running (1m49.0s), 251/500 VUs, 302786 complete and 0 interrupted iterations
default   [  73% ] 251/500 VUs  1m49.0s/2m30.0s

running (1m50.0s), 274/500 VUs, 305997 complete and 0 interrupted iterations
default   [  73% ] 274/500 VUs  1m50.0s/2m30.0s

running (1m51.0s), 296/500 VUs, 309134 complete and 0 interrupted iterations
default   [  74% ] 296/500 VUs  1m51.0s/2m30.0s

running (1m52.0s), 319/500 VUs, 312375 complete and 0 interrupted iterations
default   [  75% ] 319/500 VUs  1m52.0s/2m30.0s

running (1m53.0s), 341/500 VUs, 315626 complete and 0 interrupted iterations
default   [  75% ] 341/500 VUs  1m53.0s/2m30.0s

running (1m54.0s), 364/500 VUs, 319058 complete and 0 interrupted iterations
default   [  76% ] 364/500 VUs  1m54.0s/2m30.0s

running (1m55.0s), 386/500 VUs, 322276 complete and 0 interrupted iterations
default   [  77% ] 386/500 VUs  1m55.0s/2m30.0s

running (1m56.0s), 409/500 VUs, 325500 complete and 0 interrupted iterations
default   [  77% ] 409/500 VUs  1m56.0s/2m30.0s

running (1m57.0s), 431/500 VUs, 328844 complete and 0 interrupted iterations
default   [  78% ] 431/500 VUs  1m57.0s/2m30.0s

running (1m58.0s), 454/500 VUs, 331389 complete and 0 interrupted iterations
default   [  79% ] 454/500 VUs  1m58.0s/2m30.0s

running (1m59.0s), 476/500 VUs, 334253 complete and 0 interrupted iterations
default   [  79% ] 476/500 VUs  1m59.0s/2m30.0s

running (2m00.0s), 499/500 VUs, 337261 complete and 0 interrupted iterations
default   [  80% ] 499/500 VUs  2m00.0s/2m30.0s

running (2m01.0s), 487/500 VUs, 340151 complete and 0 interrupted iterations
default   [  81% ] 487/500 VUs  2m01.0s/2m30.0s

running (2m02.0s), 472/500 VUs, 343268 complete and 0 interrupted iterations
default   [  81% ] 472/500 VUs  2m02.0s/2m30.0s

running (2m03.0s), 458/500 VUs, 346233 complete and 0 interrupted iterations
default   [  82% ] 458/500 VUs  2m03.0s/2m30.0s

running (2m04.0s), 443/500 VUs, 349157 complete and 0 interrupted iterations
default   [  83% ] 443/500 VUs  2m04.0s/2m30.0s

running (2m05.0s), 428/500 VUs, 352302 complete and 0 interrupted iterations
default   [  83% ] 428/500 VUs  2m05.0s/2m30.0s

running (2m06.0s), 411/500 VUs, 355610 complete and 0 interrupted iterations
default   [  84% ] 411/500 VUs  2m06.0s/2m30.0s

running (2m07.0s), 397/500 VUs, 358716 complete and 0 interrupted iterations
default   [  85% ] 397/500 VUs  2m07.0s/2m30.0s

running (2m08.0s), 382/500 VUs, 361992 complete and 0 interrupted iterations
default   [  85% ] 382/500 VUs  2m08.0s/2m30.0s

running (2m09.0s), 367/500 VUs, 364944 complete and 0 interrupted iterations
default   [  86% ] 367/500 VUs  2m09.0s/2m30.0s

running (2m10.0s), 352/500 VUs, 368305 complete and 0 interrupted iterations
default   [  87% ] 352/500 VUs  2m10.0s/2m30.0s

running (2m11.0s), 336/500 VUs, 371499 complete and 0 interrupted iterations
default   [  87% ] 336/500 VUs  2m11.0s/2m30.0s

running (2m12.0s), 322/500 VUs, 374838 complete and 0 interrupted iterations
default   [  88% ] 322/500 VUs  2m12.0s/2m30.0s

running (2m13.0s), 307/500 VUs, 378025 complete and 0 interrupted iterations
default   [  89% ] 307/500 VUs  2m13.0s/2m30.0s

running (2m14.0s), 293/500 VUs, 381260 complete and 0 interrupted iterations
default   [  89% ] 293/500 VUs  2m14.0s/2m30.0s

running (2m15.0s), 278/500 VUs, 384116 complete and 0 interrupted iterations
default   [  90% ] 278/500 VUs  2m15.0s/2m30.0s

running (2m16.0s), 262/500 VUs, 386972 complete and 0 interrupted iterations
default   [  91% ] 262/500 VUs  2m16.0s/2m30.0s

running (2m17.0s), 248/500 VUs, 390034 complete and 0 interrupted iterations
default   [  91% ] 248/500 VUs  2m17.0s/2m30.0s

running (2m18.0s), 232/500 VUs, 393271 complete and 0 interrupted iterations
default   [  92% ] 232/500 VUs  2m18.0s/2m30.0s

running (2m19.0s), 217/500 VUs, 396044 complete and 0 interrupted iterations
default   [  93% ] 217/500 VUs  2m19.0s/2m30.0s

running (2m20.0s), 203/500 VUs, 399156 complete and 0 interrupted iterations
default   [  93% ] 203/500 VUs  2m20.0s/2m30.0s

running (2m21.0s), 187/500 VUs, 402067 complete and 0 interrupted iterations
default   [  94% ] 187/500 VUs  2m21.0s/2m30.0s

running (2m22.0s), 173/500 VUs, 405175 complete and 0 interrupted iterations
default   [  95% ] 173/500 VUs  2m22.0s/2m30.0s

running (2m23.0s), 157/500 VUs, 408293 complete and 0 interrupted iterations
default   [  95% ] 157/500 VUs  2m23.0s/2m30.0s

running (2m24.0s), 142/500 VUs, 411273 complete and 0 interrupted iterations
default   [  96% ] 142/500 VUs  2m24.0s/2m30.0s

running (2m25.0s), 129/500 VUs, 414240 complete and 0 interrupted iterations
default   [  97% ] 129/500 VUs  2m25.0s/2m30.0s

running (2m26.0s), 113/500 VUs, 416824 complete and 0 interrupted iterations
default   [  97% ] 113/500 VUs  2m26.0s/2m30.0s

running (2m27.0s), 096/500 VUs, 419684 complete and 0 interrupted iterations
default   [  98% ] 096/500 VUs  2m27.0s/2m30.0s

running (2m28.0s), 082/500 VUs, 422188 complete and 0 interrupted iterations
default   [  99% ] 082/500 VUs  2m28.0s/2m30.0s

running (2m29.0s), 066/500 VUs, 424787 complete and 0 interrupted iterations
default   [  99% ] 066/500 VUs  2m29.0s/2m30.0s

running (2m30.0s), 051/500 VUs, 427536 complete and 0 interrupted iterations
default   [ 100% ] 051/500 VUs  2m30.0s/2m30.0s


  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(95)<2000' p(95)=222.71ms

    http_req_failed
    ✓ 'rate<0.02' rate=0.00%


  █ TOTAL RESULTS 

    checks_total.......................: 2566014 17105.483703/s
    checks_succeeded...................: 100.00% 2566014 out of 2566014
    checks_failed......................: 0.00%   0 out of 2566014

    ✓ ingest event status is 201
    ✓ ingest event has eventId in response
    ✓ ingest event has status in response
    ✓ ingest event has processedAt in response
    ✓ spike: response time < 2000ms
    ✓ spike: response has body

    HTTP
    http_req_duration.......................................................: avg=84.84ms min=1.92ms med=68.82ms max=738ms    p(90)=182.53ms p(95)=222.71ms
      { expected_response:true }............................................: avg=84.84ms min=1.92ms med=68.82ms max=738ms    p(90)=182.53ms p(95)=222.71ms
    http_req_failed.........................................................: 0.00%  0 out of 427669
    http_reqs...............................................................: 427669 2850.91395/s

    EXECUTION
    iteration_duration......................................................: avg=84.97ms min=2ms    med=68.96ms max=738.09ms p(90)=182.71ms p(95)=222.88ms
    iterations..............................................................: 427669 2850.91395/s
    vus.....................................................................: 51     min=3           max=499
    vus_max.................................................................: 500    min=500         max=500

    NETWORK
    data_received...........................................................: 78 MB  519 kB/s
    data_sent...............................................................: 172 MB 1.1 MB/s




running (2m30.0s), 000/500 VUs, 427669 complete and 0 interrupted iterations
default ✓ [ 100% ] 000/500 VUs  2m30s

