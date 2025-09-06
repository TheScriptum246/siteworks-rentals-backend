-- Insert default roles only if they don't exist
INSERT INTO roles (name)
SELECT 'ROLE_CLIENT'
    WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_CLIENT');

INSERT INTO roles (name)
SELECT 'ROLE_STAFF'
    WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_STAFF');

INSERT INTO roles (name)
SELECT 'ROLE_ADMIN'
    WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_ADMIN');

-- Insert sample equipment only if they don't exist
-- EXCAVATORS
INSERT INTO equipment (name, description, daily_rate, category, specifications, available, image_url, created_at)
SELECT 'CAT 320 Excavator', 'Mid-size hydraulic excavator perfect for construction and demolition work', 450.00, 'EXCAVATORS', 'Operating weight: 20,000 kg, Max digging depth: 6.7m, Engine: 122 HP', true, null, NOW()
    WHERE NOT EXISTS (SELECT 1 FROM equipment WHERE name = 'CAT 320 Excavator');

INSERT INTO equipment (name, description, daily_rate, category, specifications, available, image_url, created_at)
SELECT 'Komatsu PC200 Excavator', 'Reliable excavator with excellent fuel efficiency and power', 425.00, 'EXCAVATORS', 'Operating weight: 19,900 kg, Max digging depth: 6.5m, Engine: 148 HP', true, null, NOW()
    WHERE NOT EXISTS (SELECT 1 FROM equipment WHERE name = 'Komatsu PC200 Excavator');

INSERT INTO equipment (name, description, daily_rate, category, specifications, available, image_url, created_at)
SELECT 'JCB JS130 Compact Excavator', 'Compact excavator ideal for tight spaces and urban construction', 285.00, 'EXCAVATORS', 'Operating weight: 13,000 kg, Max digging depth: 5.8m, Engine: 74 HP', true, null, NOW()
    WHERE NOT EXISTS (SELECT 1 FROM equipment WHERE name = 'JCB JS130 Compact Excavator');

-- BULLDOZERS
INSERT INTO equipment (name, description, daily_rate, category, specifications, available, image_url, created_at)
SELECT 'CAT D6T Bulldozer', 'Medium bulldozer with excellent grading and dozing capabilities', 525.00, 'BULLDOZERS', 'Operating weight: 18,500 kg, Blade capacity: 3.8m³, Engine: 215 HP', true, null, NOW()
    WHERE NOT EXISTS (SELECT 1 FROM equipment WHERE name = 'CAT D6T Bulldozer');

INSERT INTO equipment (name, description, daily_rate, category, specifications, available, image_url, created_at)
SELECT 'Komatsu D51EX Bulldozer', 'Versatile bulldozer for earthmoving and site preparation', 485.00, 'BULLDOZERS', 'Operating weight: 16,800 kg, Blade capacity: 3.2m³, Engine: 168 HP', true, null, NOW()
    WHERE NOT EXISTS (SELECT 1 FROM equipment WHERE name = 'Komatsu D51EX Bulldozer');

-- CRANES
INSERT INTO equipment (name, description, daily_rate, category, specifications, available, image_url, created_at)
SELECT 'Liebherr LTM 1050 Mobile Crane', 'All-terrain mobile crane for lifting and placing heavy materials', 850.00, 'CRANES', 'Max lifting capacity: 50 tons, Max boom length: 48m, Engine: 367 HP', true, null, NOW()
    WHERE NOT EXISTS (SELECT 1 FROM equipment WHERE name = 'Liebherr LTM 1050 Mobile Crane');

INSERT INTO equipment (name, description, daily_rate, category, specifications, available, image_url, created_at)
SELECT 'Grove RT540E Rough Terrain Crane', 'Compact rough terrain crane for construction sites', 650.00, 'CRANES', 'Max lifting capacity: 35 tons, Max boom length: 31m, Engine: 173 HP', true, null, NOW()
    WHERE NOT EXISTS (SELECT 1 FROM equipment WHERE name = 'Grove RT540E Rough Terrain Crane');

-- COMPACTORS
INSERT INTO equipment (name, description, daily_rate, category, specifications, available, image_url, created_at)
SELECT 'CAT CB24 Vibratory Compactor', 'Double drum vibratory compactor for asphalt and soil compaction', 195.00, 'COMPACTORS', 'Operating weight: 2,400 kg, Drum width: 1.2m, Vibration frequency: 67 Hz', true, null, NOW()
    WHERE NOT EXISTS (SELECT 1 FROM equipment WHERE name = 'CAT CB24 Vibratory Compactor');

INSERT INTO equipment (name, description, daily_rate, category, specifications, available, image_url, created_at)
SELECT 'Bomag BW120 Compactor', 'Single drum compactor ideal for trenches and confined areas', 165.00, 'COMPACTORS', 'Operating weight: 1,200 kg, Drum width: 0.8m, Vibration frequency: 90 Hz', true, null, NOW()
    WHERE NOT EXISTS (SELECT 1 FROM equipment WHERE name = 'Bomag BW120 Compactor');

-- GENERATORS
INSERT INTO equipment (name, description, daily_rate, category, specifications, available, image_url, created_at)
SELECT 'CAT C15 500kW Generator', 'High-capacity diesel generator for large construction sites', 285.00, 'GENERATORS', 'Power output: 500kW, Fuel capacity: 1,500L, Runtime: 24 hours', true, null, NOW()
    WHERE NOT EXISTS (SELECT 1 FROM equipment WHERE name = 'CAT C15 500kW Generator');

INSERT INTO equipment (name, description, daily_rate, category, specifications, available, image_url, created_at)
SELECT 'Caterpillar C9 200kW Generator', 'Mid-range generator perfect for medium construction projects', 165.00, 'GENERATORS', 'Power output: 200kW, Fuel capacity: 750L, Runtime: 18 hours', true, null, NOW()
    WHERE NOT EXISTS (SELECT 1 FROM equipment WHERE name = 'Caterpillar C9 200kW Generator');

INSERT INTO equipment (name, description, daily_rate, category, specifications, available, image_url, created_at)
SELECT 'Honda EU70is Portable Generator', 'Compact portable generator for small tools and lighting', 45.00, 'GENERATORS', 'Power output: 7kW, Fuel capacity: 40L, Runtime: 8 hours', true, null, NOW()
    WHERE NOT EXISTS (SELECT 1 FROM equipment WHERE name = 'Honda EU70is Portable Generator');

-- SCAFFOLDING
INSERT INTO equipment (name, description, daily_rate, category, specifications, available, image_url, created_at)
SELECT 'Layher Allround Scaffolding System', 'Complete modular scaffolding system for building construction', 85.00, 'SCAFFOLDING', 'Max height: 40m, Load capacity: 600 kg/m², Includes platforms and guardrails', true, null, NOW()
    WHERE NOT EXISTS (SELECT 1 FROM equipment WHERE name = 'Layher Allround Scaffolding System');

INSERT INTO equipment (name, description, daily_rate, category, specifications, available, image_url, created_at)
SELECT 'Kwikstage Scaffolding Kit', 'Versatile scaffolding system for various construction needs', 65.00, 'SCAFFOLDING', 'Max height: 30m, Load capacity: 500 kg/m², Quick assembly system', true, null, NOW()
    WHERE NOT EXISTS (SELECT 1 FROM equipment WHERE name = 'Kwikstage Scaffolding Kit');

-- TOOLS
INSERT INTO equipment (name, description, daily_rate, category, specifications, available, image_url, created_at)
SELECT 'Hilti TE 3000-AVR Breaker', 'Heavy-duty demolition hammer for concrete breaking', 95.00, 'TOOLS', 'Impact energy: 65J, Weight: 28kg, SDS-Max chuck system', true, null, NOW()
    WHERE NOT EXISTS (SELECT 1 FROM equipment WHERE name = 'Hilti TE 3000-AVR Breaker');

INSERT INTO equipment (name, description, daily_rate, category, specifications, available, image_url, created_at)
SELECT 'Makita DHR263Z Rotary Hammer', 'Cordless rotary hammer for drilling and light demolition', 35.00, 'TOOLS', 'Impact energy: 3.2J, Battery: 36V, SDS-PLUS chuck system', true, null, NOW()
    WHERE NOT EXISTS (SELECT 1 FROM equipment WHERE name = 'Makita DHR263Z Rotary Hammer');

INSERT INTO equipment (name, description, daily_rate, category, specifications, available, image_url, created_at)
SELECT 'STIHL TS420 Cut-off Saw', 'Professional cut-off saw for concrete, stone, and metal cutting', 55.00, 'TOOLS', 'Engine: 66.7cc, Cutting depth: 125mm, Blade diameter: 350mm', true, null, NOW()
    WHERE NOT EXISTS (SELECT 1 FROM equipment WHERE name = 'STIHL TS420 Cut-off Saw');

INSERT INTO equipment (name, description, daily_rate, category, specifications, available, image_url, created_at)
SELECT 'DeWalt DW735 Thickness Planer', 'Professional thickness planer for wood processing', 75.00, 'TOOLS', 'Cutting width: 330mm, Max depth: 3mm, Motor: 1800W', true, null, NOW()
    WHERE NOT EXISTS (SELECT 1 FROM equipment WHERE name = 'DeWalt DW735 Thickness Planer');