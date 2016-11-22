SELECT member_no AS customerId,
	   name AS customerName,
	   (case 
	        when enrollment_grade = 0 then 'Regular Member'
	        when enrollment_grade = 5 then 'VIP'
            when enrollment_grade = 10 then 'Silver'
            when enrollment_grade = 20 then 'Gold'
            when enrollment_grade = 30 then 'Platinum'
            when enrollment_grade = 40 then 'Diamond'
	        else '0' end) AS enrollmentGrade,
	   sp_all AS allSharePoints,
	   sp_buy AS personalPurchase,
	   sp_sponsor AS sponsor,
	   sp_sponsor_extra AS sponsorExtra,
	   sp_awards_basic AS awardsBasic,
	   sp_awards_keep AS awardsKeep,
	   sp_awards_new AS awardsNew,
	   sp_adjust AS adjust,
       create_time AS createDate 
       
FROM T_SP_OVERVIEW 

WHERE 1 = 1
	   