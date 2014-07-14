# --- !Ups

CREATE OR REPLACE FUNCTION resolveimage(fid bigint,protocol text,baseurl text) RETURNS text 
 AS $$ SELECT protocol||bucketname||baseurl||filequalifier||'/'||filestate||'_'||filename AS resolvedurl FROM s3file WHERE id = fid $$
 LANGUAGE SQL;
 
CREATE OR REPLACE FUNCTION getimageurl(filequalifier text,filename text,filestate text,fullbaseurl text) RETURNS text 
 AS $$ SELECT fullbaseurl||filequalifier||'/'||filestate||'_'||filename AS resolvedurl $$
 LANGUAGE SQL;
 
CREATE OR REPLACE FUNCTION updateallimage() RETURNS void AS  
$$  
	DECLARE  
	    DISTAGS RECORD;;  
	BEGIN  
	    FOR DISTAGS IN SELECT id PRID FROM product ORDER BY product.id ASC LOOP  
			UPDATE product set image_location= 'productpic1.jpg'  
			WHERE id=DISTAGS.PRID;; 
	    END LOOP;; 
	    RETURN;;  
	END  
$$  
LANGUAGE 'plpgsql';

# --- !Downs
DROP FUNCTION resolveimage(bigint, text, text);
DROP FUNCTION getimageurl(text, text, text, text);
DROP FUNCTION updateallimage();