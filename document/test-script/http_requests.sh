#!/bin/bash

# 设置循环次数
requests_per_minute=1000

# 初始化性能指标变量
total_time=0
max_time=0
min_time=99999999

while true
do
    # 生成随机数，范围在 1000 到 5000 之间
    random_number=$(( (RANDOM % 4000) + 1000 ))

    # 如果随机数大于每分钟请求的数量，就使用随机数作为请求数量
    if [ "$random_number" -gt "$requests_per_minute" ]; then
        requests_count=$random_number
    else
        requests_count=$requests_per_minute
    fi

    # 发送 HTTP 请求并记录响应时间
    for ((i = 0; i < $requests_count; i++))
    do
        # 记录开始时间
        start_time=$(date +%s%N)

        # 发送 GET 请求到指定 URL，并将响应保存到临时文件
        curl -sS -o /dev/null -w "%{time_total}\n" http://127.0.0.1:8771/demo44/prod-api/getRouters

        # 记录结束时间
        end_time=$(date +%s%N)

        # 计算响应时间（毫秒）
        response_time=$((($end_time - $start_time) / 1000000))

        # 更新性能指标
        total_time=$(($total_time + $response_time))
        if [ "$response_time" -gt "$max_time" ]; then
            max_time=$response_time
        fi
        if [ "$response_time" -lt "$min_time" ]; then
            min_time=$response_time
        fi
    done

    # 计算平均响应时间
    average_time=$(bc -l <<< "scale=2; $total_time / $requests_count")

    # 输出性能指标
    echo "请求次数: $requests_count, 平均响应时间: ${average_time}ms, 最大响应时间: ${max_time}ms, 最小响应时间: ${min_time}ms"

    # 重置性能指标变量
    total_time=0
    max_time=0
    min_time=99999999

    # 暂停 60 秒（一分钟）
    sleep 60
done

