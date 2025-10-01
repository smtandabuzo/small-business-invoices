-- Add additional indexes for better query performance
-- Wrapped in DO block for better error handling
DO $$
BEGIN
    -- Check if indexes don't exist before creating them
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_invoices_created_at') THEN
        CREATE INDEX idx_invoices_created_at ON invoices(created_at);
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_payments_created_at') THEN
        CREATE INDEX idx_payments_created_at ON payments(created_at);
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_invoices_customer_name') THEN
        CREATE INDEX idx_invoices_customer_name ON invoices(customer_name);
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_invoices_invoice_number_lower') THEN
        CREATE INDEX idx_invoices_invoice_number_lower ON invoices(LOWER(invoice_number));
    END IF;
    
EXCEPTION WHEN OTHERS THEN
    RAISE NOTICE 'Error creating indexes: %', SQLERRM;
    -- The transaction will be rolled back by Flyway
END $$;
