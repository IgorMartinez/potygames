INSERT INTO orders (id_user, total_price, status) 
VALUES
(2, 29.99, 'CONFIRMED'),
(2, 220.64, 'CANCELED');

INSERT INTO order_items (id_order, id_inventory_item, quantity, unit_price)
VALUES 
(1, 6, 1, 29.99),
(2, 5, 1, 29.99),
(2, 1, 1, 190.65);

INSERT INTO order_addresses (id_order, delivery_address, billing_address, street, number, complement, neighborhood, city, state, country, zip_code)
VALUES 
(1, true, false, 'Dakota', '522', 'Place', 'Asplenium platyneuron (L.) Britton, Sterns & Poggenb.', 'Lakhdenpokhya', 'Lycaon pictus', 'Russia', '86744-009'),
(1, false, true, 'Brentwood', '23825', 'Alley', 'Phacelia racemosa (Kellogg) Brandegee', 'Paokmotong Utara', 'Lamprotornis nitens', 'Indonesia', '15447-382'),
(2, true, false, 'Dakota', '522', 'Place', 'Asplenium platyneuron (L.) Britton, Sterns & Poggenb.', 'Lakhdenpokhya', 'Lycaon pictus', 'Russia', '86744-009'),
(2, false, true, 'Dakota', '522', 'Place', 'Asplenium platyneuron (L.) Britton, Sterns & Poggenb.', 'Lakhdenpokhya', 'Lycaon pictus', 'Russia', '86744-009');
