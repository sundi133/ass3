mat=load('C:\Users\sundi133\workspace\ass3\writeknn.txt')
[r,c] = size(mat);                           
imagesc((1:c)+0.5,(1:r)+0.5,mat);  
colormap(gray(2));  
axis equal                                  
