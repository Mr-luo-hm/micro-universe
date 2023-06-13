>  切出来最近的一个月的slow log
> 
sed -n '/2023-02-28T00/,/2023-03-31T00:00/p' slow.log  > 202303.log
> 获取执行时间最长的 10个 TOP SQL。
> 
mysqldumpslow -s t -t 10 test10.log > slow_t_top_sql.txt
> 获取平均查询时间最长的 10 个 TOP SQL。
> 
mysqldumpslow -s  at -t 10 test10.log > slow_at_top_sql.txt
> 获取锁定时间最长的 10个 TOP SQL。
> 
mysqldumpslow -s l -t 10 test10.log > slow_l_top_sql.txt
> 获取平均锁定时间最长的 10个 TOP SQL。
> 
mysqldumpslow -s al -t 10 test10.log > slow_l_top_sql.txt
> 获取返回记录最多的 10个 TOP SQL。
> 
mysqldumpslow -s r -t 10 test10.log > slow_r_top_sql.txt
> 获取平均返回记录最多的 10个 TOP SQL。
> 
mysqldumpslow -s ar -t 10 test10.log > slow_r_top_sql.txt
> 获取执行次数最多的 10个 TOP SQL。
> 
mysqldumpslow -s c -t 10 test10.log > slow_r_top_sql.txt