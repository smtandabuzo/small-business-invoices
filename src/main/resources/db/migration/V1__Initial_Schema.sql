-- Create ENUM type for payment status
CREATE TYPE payment_status AS ENUM ('PENDING', 'PAID', 'OVERDUE', 'PARTIALLY_PAID', 'CANCELLED', 'REFUNDED');

-- Create ENUM type for payment method
CREATE TYPE payment_method AS ENUM ('CREDIT_CARD', 'BANK_TRANSFER', 'CASH', 'CHECK', 'OTHER');

-- Create invoices table
CREATE TABLE invoices (
    id BIGSERIAL PRIMARY KEY,
    invoice_number VARCHAR(255) NOT NULL UNIQUE,
    customer_name VARCHAR(255) NOT NULL,
    customer_email VARCHAR(255) NOT NULL,
    issue_date DATE NOT NULL,
    due_date DATE NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    amount_paid DECIMAL(10,2) DEFAULT 0.00,
    status payment_status NOT NULL,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_amount_positive CHECK (amount >= 0),
    CONSTRAINT chk_amount_paid_positive CHECK (amount_paid >= 0),
    CONSTRAINT chk_dates_valid CHECK (due_date >= issue_date)
);

-- Create payments table
CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    invoice_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_date TIMESTAMP WITH TIME ZONE NOT NULL,
    payment_method payment_method NOT NULL,
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_invoice FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE CASCADE,
    CONSTRAINT chk_payment_amount_positive CHECK (amount > 0)
);

-- Create indexes for better query performance
CREATE INDEX idx_invoices_customer_email ON invoices(customer_email);
CREATE INDEX idx_invoices_status ON invoices(status);
CREATE INDEX idx_invoices_due_date ON invoices(due_date);
CREATE INDEX idx_payments_invoice_id ON payments(invoice_id);
CREATE INDEX idx_payments_payment_date ON payments(payment_date);

-- Create a function to update the updated_at timestamp
CREATE OR REPLACE FUNCTION update_modified_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create triggers to update timestamps
CREATE TRIGGER update_invoices_modtime
BEFORE UPDATE ON invoices
FOR EACH ROW EXECUTE FUNCTION update_modified_column();

CREATE TRIGGER update_payments_modtime
BEFORE UPDATE ON payments
FOR EACH ROW EXECUTE FUNCTION update_modified_column();

-- Create a function to update invoice status based on payments
CREATE OR REPLACE FUNCTION update_invoice_status()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE invoices i
    SET 
        amount_paid = COALESCE((
            SELECT SUM(amount)
            FROM payments p
            WHERE p.invoice_id = i.id
        ), 0),
        status = CASE
            WHEN i.amount <= 0 THEN 'PENDING'::payment_status
            WHEN COALESCE((
                SELECT SUM(amount)
                FROM payments p
                WHERE p.invoice_id = i.id
            ), 0) >= i.amount THEN 'PAID'::payment_status
            WHEN COALESCE((
                SELECT SUM(amount)
                FROM payments p
                WHERE p.invoice_id = i.id
            ), 0) > 0 THEN 'PARTIALLY_PAID'::payment_status
            WHEN i.due_date < CURRENT_DATE THEN 'OVERDUE'::payment_status
            ELSE 'PENDING'::payment_status
        END
    WHERE i.id = COALESCE(NEW.invoice_id, OLD.invoice_id);
    
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Create triggers to update invoice status when payments change
CREATE TRIGGER update_invoice_after_payment
AFTER INSERT OR UPDATE OR DELETE ON payments
FOR EACH ROW EXECUTE FUNCTION update_invoice_status();

-- Create a function to validate payment amount
CREATE OR REPLACE FUNCTION validate_payment_amount()
RETURNS TRIGGER AS $$
DECLARE
    invoice_amount DECIMAL(10,2);
    total_paid DECIMAL(10,2);
BEGIN
    SELECT amount, COALESCE((
        SELECT SUM(amount)
        FROM payments
        WHERE invoice_id = NEW.invoice_id
          AND id != COALESCE(NEW.id, -1)  -- Exclude current payment if updating
    ), 0) + NEW.amount
    INTO invoice_amount, total_paid
    FROM invoices
    WHERE id = NEW.invoice_id;

    IF total_paid > invoice_amount THEN
        RAISE EXCEPTION 'Payment amount exceeds remaining balance';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger to validate payment amount
CREATE TRIGGER validate_payment_amount
BEFORE INSERT OR UPDATE ON payments
FOR EACH ROW EXECUTE FUNCTION validate_payment_amount();
