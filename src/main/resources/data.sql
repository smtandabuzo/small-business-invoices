-- Insert sample invoices
INSERT INTO invoices (invoice_number, client_name, amount, due_date, status) VALUES
('INV-2023-001', 'Acme Corp', 1500.00, '2023-12-31', 'PENDING'),
('INV-2023-002', 'Globex Inc', 2500.50, '2023-12-15', 'PAID'),
('INV-2023-003', 'Soylent Corp', 980.75, '2023-12-20', 'OVERDUE');

-- Insert sample payments
INSERT INTO payments (invoice_id, amount, payment_date, payment_method, reference_number) VALUES
(2, 1000.00, '2023-11-01 10:30:00', 'BANK_TRANSFER', 'BANK-REF-001'),
(2, 1500.50, '2023-11-15 14:45:00', 'CREDIT_CARD', 'CC-2023-001'),
(3, 500.00, '2023-11-20 09:15:00', 'PAYPAL', 'PP-2023-001');
