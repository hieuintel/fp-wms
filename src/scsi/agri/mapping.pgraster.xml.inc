 <spatialExtension name="pgraster"/>
 <mapping>
      <masterTable name="mosaic" >
              <coverageNameAttribute name="NAME"/>
              <maxXAttribute name="maxX"/>
              <maxYAttribute name="maxY"/>
              <minXAttribute name="minX"/>
              <minYAttribute name="minY"/>
              <resXAttribute name="resX"/>
              <resYAttribute name="resY"/>
              <tileTableNameAtribute  name="TileTable" />
      </masterTable>
      <tileTable>
              <blobAttributeName name="rast" />
      </tileTable>
</mapping>