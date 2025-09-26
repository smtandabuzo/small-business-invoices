-- Add soft delete support
ALTER TABLE invoices ADD COLUMN deleted BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE payments ADD COLUMN deleted BOOLEAN NOT NULL DEFAULT FALSE;

-- Drop existing triggers and functions that need to be updated
DROP TRIGGER IF EXISTS update_invoice_after_payment ON payments;
DROP FUNCTION IF EXISTS update_invoice_status();

-- Update the function to handle soft deletes
CREATE OR REPLACE FUNCTION update_invoice_status()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE invoices i
    SET 
        amount_paid = COALESCE((
            SELECT SUM(amount)
            FROM payments p
            WHERE p.invoice_id = i.id
            AND p.deleted = FALSE
        ), 0),
        status = CASE
            WHEN i.amount <= 0 THEN 'PENDING'::payment_status
            WHEN COALESCE((
                SELECT SUM(amount)
                FROM payments p
                WHERE p.invoice_id = i.id
                AND p.deleted = FALSE
            ), 0) >= i.amount THEN 'PAID'::payment_status
            WHEN COALESCE((
                SELECT SUM(amount)
                FROM payments p
                WHERE p.invoice_id = i.id
                AND p.deleted = FALSE
            ), 0) > 0 THEN 'PARTIALLY_PAID'::payment_status
            WHEN i.due_date < CURRENT_DATE THEN 'OVERDUE'::payment_status
            ELSE 'PENDING'::payment_status
        END
    WHERE i.id = COALESCE(NEW.invoice_id, OLD.invoice_id)
    AND i.deleted = FALSE;
    
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Recreate the trigger
CREATE TRIGGER update_invoice_after_payment
AFTER INSERT OR UPDATE OR DELETE ON payments
FOR EACH ROW EXECUTE FUNCTION update_invoice_status();

-- Create a view for non-deleted invoices
CREATE OR REPLACE VIEW active_invoices AS
SELECT * FROM invoices WHERE deleted = FALSE;

-- Create a view for non-deleted payments
CREATE OR REPLACE VIEW active_payments AS
SELECT * FROM payments WHERE deleted = FALSE;
