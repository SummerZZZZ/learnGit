SELECT tmo.member_no AS CustomerId,
       tmo.name AS distributorName,
       tmo.doc_no AS OrderId,
       tmo.buy_bv AS BV,
       tmo.order_date orderDate,
       tmo.sp  AS sharedPoint 
FROM T_SP_Detail_buy tmo 
WHERE 1=1

