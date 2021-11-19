# JOINS
*	```KStream/KTable``` must have a valid key
*	All topics of the join must have the same number of partitions
*	Data in topics must be co-partitioned
*	co-partitioning is not mandatory for ```KStream-GlobalKTable``` joins
*	```Non-Key``` based joins are allowed with KStream-GlobalKTable joins
*	```KStream``` must be on the left side of the join


##   Kinds of joins

|JOIN OPERATION   | RESULT    | JOIN TYPES              | Feature   |
| :-------------- |:--------- |:----------------------- |:--------- |
| inner join      | KStream   |inner, left, right, full |window     |
| left join       | KTable    |inner, left, right, full |non-window |
| right join(swap)| KStream   |inner, left, full        |non-window |
| zebra stripes   | KStream   |inner, left              |non-window |