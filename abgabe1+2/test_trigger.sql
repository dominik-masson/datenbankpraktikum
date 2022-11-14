

INSERT INTO public.review (customer, product, description, summary, points, helpful, timestamp) VALUES (4269, 'B0007YH7QE', 'test review', 'wild', 1, 1, '2016-10-03 14:53:18.000000')


UPDATE public.review SET points = 4 WHERE customer = 4269 AND product LIKE 'B0007YH7QE' AND timestamp = '2016-10-03 14:53:18.000000'


DELETE FROM public.review WHERE customer = 4269 AND product LIKE 'B0007YH7QE' AND timestamp = '2016-10-03 14:53:18.000000'